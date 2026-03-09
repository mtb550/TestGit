package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.*;
import testGit.projectPanel.ProjectPanel;
import testGit.util.TestCaseSorter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class TestRunCreationUI implements Disposable {
    private final List<TestCase> initialTestCases;
    private final Set<Integer> initialTestCaseUids;
    JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());
    private CheckboxTree checklistTree;
    private TestRun currentTestRun;
    private TestRun metadata;
    private VirtualFile currentFile;
    private Map<UUID, TestRun.TestRunItems> resultsMap;
    private TestRunMetadataHeader metadataHeader;

    public TestRunCreationUI(List<TestCase> initialTestCases) {
        System.out.println("[TRACE] TestRunCreationUI constructor started.");
        this.initialTestCases = TestCaseSorter.sortTestCases(initialTestCases);
        this.initialTestCaseUids = this.initialTestCases.stream()
                .map(TestCase::getUid)
                .collect(Collectors.toSet());
    }

    public JComponent createEditorPanel(DefaultTreeModel testCaseModel, String savePathString, ProjectPanel projectPanel) {
        System.out.println("[TRACE] createEditorPanel() started.");

        System.out.println("[TRACE] Converting DefaultTreeModel to CheckedTreeNodes...");
        CheckedTreeNode root = convertToCheckedNodes((DefaultMutableTreeNode) testCaseModel.getRoot());
        System.out.println("[TRACE] Tree conversion complete.");

        mainPanel = new JBPanel<>(new BorderLayout());

        // NEW: Add the metadata panel at the top (NORTH)
        // Initialize and add the clean header class
        metadataHeader = new TestRunMetadataHeader();
        mainPanel.add(metadataHeader.getPanel(), BorderLayout.NORTH);

        System.out.println("[TRACE] Initializing CheckboxTree...");
        checklistTree = new CheckboxTree(createRenderer(), root, new CheckboxTreeBase.CheckPolicy(true, true, true, true));
        TreeUtil.expandAll(checklistTree);

        mainPanel.add(new JBScrollPane(checklistTree), BorderLayout.CENTER);
        mainPanel.add(createSaveButton(root, savePathString, projectPanel), BorderLayout.SOUTH);

        System.out.println("[TRACE] createEditorPanel() finished.");
        return mainPanel;
    }

    private CheckboxTree.CheckboxTreeCellRenderer createRenderer() {
        return new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(@NotNull JTree tree, @NotNull Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof CheckedTreeNode node) {
                    Object userObj = node.getUserObject();

                    if (userObj instanceof Directory dir) {
                        getTextRenderer().append(dir.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                    } else if (userObj instanceof TestCase tc) {
                        // Render TestCase name and status
                        TestRun.TestRunItems result = findResultFor(tc.getId());
                        SimpleTextAttributes mainStyle = SimpleTextAttributes.REGULAR_ATTRIBUTES;
                        String statusText = " [Pending]";

                        if (result != null) {
                            switch (result.getStatus()) {
                                case "PASSED" -> {
                                    mainStyle = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.BLUE);
                                    statusText = " [Passed]";
                                }
                                case "FAILED" -> {
                                    mainStyle = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED);
                                    statusText = " [Failed]";
                                }
                                case "BLOCKED" -> {
                                    mainStyle = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE);
                                    statusText = " [Blocked]";
                                }
                            }
                        }

                        getTextRenderer().append(tc.getTitle(), mainStyle);
                        getTextRenderer().append(statusText, SimpleTextAttributes.GRAYED_ATTRIBUTES);
                    } else if (userObj instanceof String str) {
                        // Fallback for the root node string
                        getTextRenderer().append(str, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
                    }
                }
            }
        };
    }

    private JButton createSaveButton(CheckedTreeNode root, String savePathString, ProjectPanel projectPanel) {
        JButton saveButton = new JButton("Save Test Run");
        saveButton.addActionListener(e -> {
            System.out.println("[TRACE] Save button clicked!");
            if (!metadataHeader.validate()) {
                JOptionPane.showMessageDialog(mainPanel, "Build number is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            metadataHeader.applyToMetadata(this.metadata);
            saveSelectedToJSON(root, savePathString, projectPanel);
        });
        return saveButton;
    }

    private CheckedTreeNode convertToCheckedNodes(DefaultMutableTreeNode node) {
        Object userObj = node.getUserObject();
        CheckedTreeNode newNode = new CheckedTreeNode(userObj);

        // If this node is a TestCase, check if it should be selected initially
        if (userObj instanceof TestCase tc && isAlreadyInRun(tc)) {
            newNode.setChecked(true);
            System.out.println("[TRACE] Auto-checking TestCase: " + tc.getTitle());
        }

        // Recursively convert all children
        for (int i = 0; i < node.getChildCount(); i++) {
            newNode.add(convertToCheckedNodes((DefaultMutableTreeNode) node.getChildAt(i)));
        }

        return newNode;
    }

    private boolean isAlreadyInRun(TestCase tc) {
        return initialTestCaseUids != null && initialTestCaseUids.contains(tc.getUid());
    }

    private void saveSelectedToJSON(CheckedTreeNode root, String baseProjectPath, ProjectPanel projectPanel) {
        System.out.println("[TRACE] saveSelectedToJSON() started.");

        // Use the baseProjectPath directly to avoid duplicate "testRuns" folders
        File testRunsDir = new File(baseProjectPath);

        TestRun run = this.currentTestRun != null ? this.currentTestRun : new TestRun();

        if (this.metadata != null) {
            run.setBuildNumber(metadata.getBuildNumber());
            run.setPlatform(metadata.getPlatform());
            run.setLanguage(metadata.getLanguage());
            run.setBrowser(metadata.getBrowser());
            run.setDeviceType(metadata.getDeviceType());
        }

        assert metadata != null;
        String fileName = DirectoryType.TR.name() + "_" + metadata.getBuildNumber() + "_" + DirectoryStatus.AC.name() + ".json";
        File finalOutputFile = new File(testRunsDir, fileName);

        run.setRunName(fileName);
        run.setCreatedAt(LocalDateTime.now());
        run.setStatus(TestRunStatus.CREATED);

        List<TestRun.TestRunItems> items = new ArrayList<>();
        collectCheckedItems(root, items);
        run.setResults(items);

        try {
            Config.getMapper().writerWithDefaultPrettyPrinter().writeValue(finalOutputFile, run);
            System.out.println("[TRACE] Test Run saved successfully to: " + finalOutputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[TRACE-ERROR] Failed to save JSON.");
            e.printStackTrace(System.err);
        }

        projectPanel.getTestRunTabController().buildTreeAsync(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem());
        FileEditorManager.getInstance(Config.getProject()).closeFile(currentFile);
    }

    private void collectCheckedItems(CheckedTreeNode node, List<TestRun.TestRunItems> items) {
        if (node.getUserObject() instanceof TestCase tc && node.isChecked()) {
            TestRun.TestRunItems item = new TestRun.TestRunItems();
            item.setTestCaseId(UUID.fromString(tc.getId()));
            item.setStatus("PENDING");

            // Navigate up the tree to find the parent Directory (Project/Test Set)
            Object rootObject = ((DefaultMutableTreeNode) node.getRoot()).getUserObject();
            if (rootObject instanceof Directory rootDir) {
                item.setProject(rootDir.getFileName());
            } else if (rootObject instanceof String str) {
                item.setProject(str); // Fallback if root is a String
            }
            items.add(item);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            collectCheckedItems((CheckedTreeNode) node.getChildAt(i), items);
        }
    }

    private TestRun.TestRunItems findResultFor(String testCaseId) {
        if (resultsMap == null) return null;
        try {
            return resultsMap.get(UUID.fromString(testCaseId));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void dispose() {
    }
}