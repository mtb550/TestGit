package testGit.projectPanel.testCaseTab;

import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.StatusText;
import lombok.Getter;
import testGit.projectPanel.ProjectPanel;
import testGit.projectPanel.TransferHandlerImpl;
import testGit.projectPanel.projectSelector.TestProjectSelector;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.HashSet;
import java.util.Set;

import static testGit.util.TestCasesDirectoryMapper.buildTreeAsync;

public class TestCaseTabController {
    private final ProjectPanel projectPanel;
    @Getter
    private final SimpleTree tree;

    public TestCaseTabController(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.tree = new SimpleTree();
    }

    public void init() {
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);

        Set<DefaultMutableTreeNode> sharedCutNodes = new HashSet<>();
        tree.setCellRenderer(new TestCaseRenderer(sharedCutNodes));

        TransferHandlerImpl transferHandler = new TransferHandlerImpl(tree, sharedCutNodes);
        tree.setTransferHandler(transferHandler);
        ShortcutHandler.register(projectPanel, tree, transferHandler);
        tree.addMouseListener(new MouseAdapterImpl(projectPanel));

        TestProjectSelector projectSelector = projectPanel.getTestProjectSelector();
        if (projectSelector.getTestProjectList().getSize() > 0)
            buildTreeAsync(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem(), tree);
        else {
            System.out.println("no test cases and project");
            showEmptyState();
        }
    }

    public void showEmptyState() {
        tree.setModel(new DefaultTreeModel(null));

        StatusText emptyText = tree.getEmptyText();
        emptyText.setText("No test projects found.");
        emptyText.appendLine("Press ");
        emptyText.appendText("+ button", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        emptyText.appendText(" At the top panel");
        emptyText.appendLine("To create new test project ");

        tree.setRootVisible(false);
    }


}