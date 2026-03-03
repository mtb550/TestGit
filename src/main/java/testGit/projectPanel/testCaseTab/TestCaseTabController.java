package testGit.projectPanel.testCaseTab;

import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;
import testGit.projectPanel.ProjectPanel;
import testGit.projectPanel.TransferHandlerImpl;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
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

        buildTreeAsync(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem(), tree);
    }
}