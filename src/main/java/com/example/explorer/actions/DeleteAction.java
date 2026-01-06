package com.example.explorer.actions;

import com.example.explorer.ComboBoxProjectSelector;
import com.example.explorer.Panel;
import com.example.pojo.Directory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;

public class DeleteAction extends AnAction {
    private final Panel panel;
    private final SimpleTree tree;

    public DeleteAction(final Panel panel) {
        super("❌ Delete");
        this.panel = panel;
        this.tree = panel.getTestCaseTree();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (tree == null) return;

        TreePath path = tree.getSelectionPath();
        if (path == null) return;

        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        if (!(selectedNode.getUserObject() instanceof Directory treeItem)) return;

        int confirm = Messages.showYesNoDialog(
                "Are you sure you want to delete '" + treeItem.getName() + "' and all its folders/files on disk?",
                "Confirm Delete",
                Messages.getQuestionIcon()
        );
        if (confirm != Messages.YES) return;

        try {
            File fileOnDisk = treeItem.getFilePath().toFile();
            if (fileOnDisk.exists()) {
                boolean success = FileUtil.delete(fileOnDisk);

                if (success) {
                    System.out.println("Success! Deleted: " + treeItem.getFilePath());
                    //refreshPath(treeItem.getFilePath().getParent());

                } else {
                    System.out.println("Could not delete folder. It might be in use. Delete Failed.");
                    return;
                }
            }

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.removeNodeFromParent(selectedNode);

            // بعد نجاح الحذف، حدث الـ ComboBox بسهولة
            if (panel.getProjectSelector() != null) {
                panel.getProjectSelector().reloadProjects();
                //panel.loadAllProjects();
                panel.filterByProject(ComboBoxProjectSelector.comboBox.getItem());
            }

        } catch (Exception ex) {
            //Messages.showErrorDialog("Error during delete: " + ex.getMessage(), "Error");
            System.out.println("Error during delete: " + ex.getMessage());
        }
    }
}