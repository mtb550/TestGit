package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import testGit.editorPanel.StatusBar;
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
public class TestRunEditorUI implements Disposable {

    // --- Shared ---
    private final VirtualFileImpl vf;
    private final List<TestCase> initialTestCases;
    private final Set<Integer> initialTestCaseUids;
    private JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());

    // --- Opening-mode state ---
    private TestRunCard selectedCard = null;
    private int currentPage = 1;
    private int pageSize = 10;
    // cardListPanel and statusBar are kept as fields so renderPage() can reach them
    private JPanel cardListPanel;
    private StatusBar statusBar;

    // --- Creation-mode state ---
    private CheckboxTree checklistTree;
    private TestRun metadata;
    private VirtualFile currentFile;
    private Map<UUID, TestRun.TestRunItems> resultsMap;
    private TestRunMetadataHeader metadataHeader;

    public TestRunEditorUI(VirtualFileImpl vf) {
        this.vf = vf;
        this.metadata = vf.getMetadata();
        this.currentFile = vf;

        List<TestCase> cases = vf.getTestCases() != null ? vf.getTestCases() : Collections.emptyList();
        this.initialTestCases = TestCaseSorter.sortTestCases(cases);
        this.initialTestCaseUids = this.initialTestCases.stream()
                .map(TestCase::getUid)
                .collect(Collectors.toSet());
    }

    public JComponent createEditorPanel() {
        return switch (vf.getEditorType()) {
            case TEST_RUN_OPENING -> buildOpeningPanel();
            case TEST_RUN_CREATION ->
                    buildCreationPanel(vf.getTestCasesTreeModel(), vf.getRunPath(), vf.getProjectPanel());
            default -> throw new IllegalArgumentException("Unsupported editor type: " + vf.getEditorType());
        };
    }

    // -------------------------------------------------------------------------
    // Opening mode
    // -------------------------------------------------------------------------

    private JComponent buildOpeningPanel() {
        cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(UIUtil.getTreeBackground());
        cardListPanel.setOpaque(true);

        JBScrollPane scrollPane = new JBScrollPane(cardListPanel);
        scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);

        // Embedded StatusBar — wired here, invisible to any caller
        statusBar = new StatusBar();
        wirePaginationButtons();

        mainPanel = new JBPanel<>(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        renderPage();
        return mainPanel;
    }

    private void renderPage() {
        // Sync pageSize from the StatusBar's text field (user may have edited it)
        try {
            int parsed = Integer.parseInt(statusBar.getPageSizeField().getText().trim());
            if (parsed > 0) pageSize = parsed;
        } catch (NumberFormatException ignored) {
        }

        int total = initialTestCases.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) total / pageSize));
        currentPage = Math.max(1, Math.min(currentPage, totalPages));

        int fromIndex = (currentPage - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<TestCase> pageItems = initialTestCases.subList(fromIndex, toIndex);

        cardListPanel.removeAll();
        for (int i = 0; i < pageItems.size(); i++) {
            TestRunCard card = new TestRunCard(fromIndex + i, pageItems.get(i));
            card.setSelectionListener(this::handleCardSelected);
            cardListPanel.add(card);
        }
        cardListPanel.add(Box.createVerticalGlue());
        cardListPanel.revalidate();
        cardListPanel.repaint();

        // Reset scroll position to top after page change
        SwingUtilities.invokeLater(() -> {
            JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, cardListPanel);
            if (sp != null) sp.getVerticalScrollBar().setValue(0);
        });

        statusBar.updatePaginationState(currentPage, totalPages, pageItems.size(), total);
        selectedCard = null;
    }

    private void wirePaginationButtons() {
        statusBar.getFirstButton().addActionListener(e -> {
            currentPage = 1;
            renderPage();
        });
        statusBar.getPrevButton().addActionListener(e -> {
            currentPage--;
            renderPage();
        });
        statusBar.getNextButton().addActionListener(e -> {
            currentPage++;
            renderPage();
        });
        statusBar.getLastButton().addActionListener(e -> {
            int total = initialTestCases.size();
            currentPage = Math.max(1, (int) Math.ceil((double) total / pageSize));
            renderPage();
        });
        // Allow committing a new page size by pressing Enter in the field
        statusBar.getPageSizeField().addActionListener(e -> {
            currentPage = 1;
            renderPage();
        });
    }

    private void handleCardSelected(TestRunCard newlySelected) {
        if (selectedCard != null && selectedCard != newlySelected) {
            selectedCard.deselect();
        }
        selectedCard = newlySelected;
    }

    // -------------------------------------------------------------------------
    // Creation mode
    // -------------------------------------------------------------------------

    private JComponent buildCreationPanel(DefaultTreeModel testCaseModel, String savePath, ProjectPanel projectPanel) {
        CheckedTreeNode root = convertToCheckedNodes((DefaultMutableTreeNode) testCaseModel.getRoot());

        mainPanel = new JBPanel<>(new BorderLayout());

        // Embedded metadata header — owned here, not exposed to callers
        metadataHeader = new TestRunMetadataHeader();
        mainPanel.add(metadataHeader.getPanel(), BorderLayout.NORTH);

        checklistTree = new CheckboxTree(createTreeRenderer(), root,
                new CheckboxTreeBase.CheckPolicy(true, true, true, true));
        TreeUtil.expandAll(checklistTree);

        mainPanel.add(new JBScrollPane(checklistTree), BorderLayout.CENTER);
        mainPanel.add(createSaveButton(root, savePath, projectPanel), BorderLayout.SOUTH);

        return mainPanel;
    }

    private CheckboxTree.CheckboxTreeCellRenderer createTreeRenderer() {
        return new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(@NotNull JTree tree, @NotNull Object value, boolean selected,
                                          boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (!(value instanceof CheckedTreeNode node)) return;
                Object userObj = node.getUserObject();

                if (userObj instanceof Directory dir) {
                    getTextRenderer().append(dir.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                } else if (userObj instanceof TestCase tc) {
                    renderTestCaseNode(tc);
                } else if (userObj instanceof String str) {
                    getTextRenderer().append(str, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
                }
            }

            private void renderTestCaseNode(TestCase tc) {
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

        if (userObj instanceof TestCase tc && initialTestCaseUids.contains(tc.getUid())) {
            newNode.setChecked(true);
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            newNode.add(convertToCheckedNodes((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return newNode;
    }

    private void saveSelectedToJSON(CheckedTreeNode root, String savePath, ProjectPanel projectPanel) {
        TestRun run = new TestRun();
        if (metadata != null) {
            run.setBuildNumber(metadata.getBuildNumber());
            run.setPlatform(metadata.getPlatform());
            run.setLanguage(metadata.getLanguage());
            run.setBrowser(metadata.getBrowser());
            run.setDeviceType(metadata.getDeviceType());
        }

        String fileName = DirectoryType.TR.name() + "_" + metadata.getBuildNumber() + "_" + DirectoryStatus.AC.name() + ".json";
        run.setRunName(fileName);
        run.setCreatedAt(LocalDateTime.now());
        run.setStatus(TestRunStatus.CREATED);

        List<TestRun.TestRunItems> items = new ArrayList<>();
        collectCheckedItems(root, items);
        run.setResults(items);

        try {
            Config.getMapper().writerWithDefaultPrettyPrinter().writeValue(new File(savePath, fileName), run);
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
