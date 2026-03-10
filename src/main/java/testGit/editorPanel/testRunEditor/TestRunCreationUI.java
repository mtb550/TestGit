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

    private CheckboxTree checklistTree;
    private TestRun metadata;
    private VirtualFile currentFile;
    private Map<UUID, TestRun.TestRunItems> resultsMap;
    private TestRunMetadataHeader metadataHeader;

    private JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());

    public TestRunCreationUI(List<TestCase> initialTestCases) {
        this.initialTestCases = TestCaseSorter.sortTestCases(initialTestCases);
        this.initialTestCaseUids = this.initialTestCases.stream()
                .map(TestCase::getUid)
                .collect(Collectors.toSet());
    }

    public JComponent createEditorPanel(DefaultTreeModel testCaseModel, String savePath, ProjectPanel projectPanel) {
        CheckedTreeNode root = convertToCheckedNodes((DefaultMutableTreeNode) testCaseModel.getRoot());

        mainPanel = new JBPanel<>(new BorderLayout());

        metadataHeader = new TestRunMetadataHeader();
        mainPanel.add(metadataHeader.getPanel(), BorderLayout.NORTH);

        checklistTree = new CheckboxTree(createRenderer(), root,
                new CheckboxTreeBase.CheckPolicy(true, true, true, true));
        TreeUtil.expandAll(checklistTree);

        mainPanel.add(new JBScrollPane(checklistTree), BorderLayout.CENTER);
        mainPanel.add(createSaveButton(root, savePath, projectPanel), BorderLayout.SOUTH);

        return mainPanel;
    }

    private CheckboxTree.CheckboxTreeCellRenderer createRenderer() {
        return new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(@NotNull JTree tree, @NotNull Object value, boolean selected,
                                          boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (!(value instanceof CheckedTreeNode node)) return;
                Object userObj = node.getUserObject();

                if (userObj instanceof Directory dir) {
                    getTextRenderer().append(dir.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                } else if (userObj instanceof TestCase tc) {
                    renderTestCase(tc);
                } else if (userObj instanceof String str) {
                    getTextRenderer().append(str, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
                }
            }

            private void renderTestCase(TestCase tc) {
                TestRun.TestRunItems result = findResultFor(tc.getId());
                SimpleTextAttributes style = SimpleTextAttributes.REGULAR_ATTRIBUTES;
                String statusText = " [Pending]";

                if (result != null) {
                    switch (result.getStatus()) {
                        case "PASSED" -> {
                            style = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.BLUE);
                            statusText = " [Passed]";
                        }
                        case "FAILED" -> {
                            style = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED);
                            statusText = " [Failed]";
                        }
                        case "BLOCKED" -> {
                            style = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE);
                            statusText = " [Blocked]";
                        }
                    }
                }
                getTextRenderer().append(tc.getTitle(), style);
                getTextRenderer().append(statusText, SimpleTextAttributes.GRAYED_ATTRIBUTES);
            }
        };
    }

    private JButton createSaveButton(CheckedTreeNode root, String savePath, ProjectPanel projectPanel) {
        JButton saveButton = new JButton("Save Test Run");
        saveButton.addActionListener(e -> {
            if (!metadataHeader.validate()) {
                JOptionPane.showMessageDialog(mainPanel, "Build number is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            metadataHeader.applyToMetadata(this.metadata);
            saveSelectedToJSON(root, savePath, projectPanel);
        });
        return saveButton;
    }

    private CheckedTreeNode convertToCheckedNodes(DefaultMutableTreeNode node) {
        Object userObj = node.getUserObject();
        CheckedTreeNode newNode = new CheckedTreeNode(userObj);

        if (userObj instanceof TestCase tc && isAlreadyInRun(tc)) {
            newNode.setChecked(true);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            newNode.add(convertToCheckedNodes((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return newNode;
    }

    private boolean isAlreadyInRun(TestCase tc) {
        return initialTestCaseUids != null && initialTestCaseUids.contains(tc.getUid());
    }

    private void saveSelectedToJSON(CheckedTreeNode root, String savePath, ProjectPanel projectPanel) {
        TestRun run = Optional.ofNullable(this.metadata).map(m -> {
            TestRun r = new TestRun();
            r.setBuildNumber(m.getBuildNumber());
            r.setPlatform(m.getPlatform());
            r.setLanguage(m.getLanguage());
            r.setBrowser(m.getBrowser());
            r.setDeviceType(m.getDeviceType());
            return r;
        }).orElseGet(TestRun::new);

        String fileName = DirectoryType.TR.name() + "_" + metadata.getBuildNumber() + "_" + DirectoryStatus.AC.name() + ".json";
        File outputFile = new File(savePath, fileName);

        run.setRunName(fileName);
        run.setCreatedAt(LocalDateTime.now());
        run.setStatus(TestRunStatus.CREATED);

        List<TestRun.TestRunItems> items = new ArrayList<>();
        collectCheckedItems(root, items);
        run.setResults(items);

        try {
            Config.getMapper().writerWithDefaultPrettyPrinter().writeValue(outputFile, run);
        } catch (Exception e) {
            System.err.println("Failed to save Test Run: " + e.getMessage());
            e.printStackTrace(System.err);
        }

        projectPanel.getTestRunTabController().buildTreeAsync(
                projectPanel.getTestProjectSelector().getSelectedTestProject().getItem());
        FileEditorManager.getInstance(Config.getProject()).closeFile(currentFile);
    }

    private void collectCheckedItems(CheckedTreeNode node, List<TestRun.TestRunItems> items) {
        if (node.getUserObject() instanceof TestCase tc && node.isChecked()) {
            TestRun.TestRunItems item = new TestRun.TestRunItems();
            item.setTestCaseId(UUID.fromString(tc.getId()));
            item.setStatus("PENDING");

            Object rootObj = ((DefaultMutableTreeNode) node.getRoot()).getUserObject();
            item.setProject(rootObj instanceof Directory d ? d.getFileName() : String.valueOf(rootObj));
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
