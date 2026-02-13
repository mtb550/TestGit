package testGit.projectPanel.testPlanTab;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.ui.treeStructure.SimpleTree;
import testGit.editorPanel.TestPlanEditor;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;
import testGit.projectPanel.ProjectPanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestPlanMouseAdapter extends MouseAdapter {
    private final SimpleTree tree;
    private final ProjectPanel projectPanel;

    public TestPlanMouseAdapter(final ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.tree = projectPanel.getTestPlanTree();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("TestPlanMouseAdapter.mouseClicked()");

        TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
        if (selPath == null) {
            System.out.println("selPath == null");
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        Object userObject = node.getUserObject();

        if (!(userObject instanceof Directory treeItem)) {
            System.out.println("userObject not instanceof Directory");
            return;
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            System.out.println("right click test plan");
            TestPlanEditor.open(treeItem.getFilePath(), projectPanel);

            int row = tree.getClosestRowForLocation(e.getX(), e.getY());
            tree.setSelectionRow(row);

            ContextMenu contextMenu = new ContextMenu(projectPanel);

            ActionPopupMenu popupMenu = ActionManager.getInstance()
                    .createActionPopupMenu(ActionPlaces.TOOLWINDOW_POPUP, contextMenu);

            popupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
        } else if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) && treeItem.getType() == DirectoryType.TR) {
            System.out.println("double click test plan");
            TestPlanEditor.open(treeItem.getFilePath(), projectPanel);
        }

    }

}
