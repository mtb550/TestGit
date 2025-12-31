package com.example.explorer.actions;

import com.example.explorer.ExplorerPanel;
import com.example.pojo.Config;
import com.example.pojo.Directory;
import com.example.util.NodeType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AddProjectAction extends AnAction {
    private final ExplorerPanel panel;
    private final SimpleTree tree;

    public AddProjectAction(ExplorerPanel panel) {
        super("New Project", "Add new project", AllIcons.General.Add);
        this.panel = panel;
        this.tree = this.panel.getProjectTree();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String name = Messages.showInputDialog("Enter project name:", "Add New Project", null);
        if (name == null || name.isBlank()) return;

        // دمج المسار الأساسي مع اسم المجلد الجديد
        //Path fullPath = Config.rootFolder.toPath().resolve(name);

        Directory newProject = new Directory()
                .setType(NodeType.PROJECT.getCode())
                .setId(100)
                .setName(name)
                .setActive(1);
        newProject.setFileName(newProject.getType() + "_" + newProject.getId() + "_" + newProject.getName() + "_" + newProject.getActive());
        newProject.setFilePath(Config.rootFolder.toPath().resolve(newProject.getFileName()));
        newProject.setFile(new File(newProject.getFileName()));

        try {
            Files.createDirectories(newProject.getFilePath());
            System.out.println("Success! Path created: " + newProject.getFilePath());
        } catch (IOException ee) {
            System.err.println("Could not create folder: " + ee.getMessage());
        }

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        DefaultMutableTreeNode newProjectNode = new DefaultMutableTreeNode(newProject);

        model.insertNodeInto(newProjectNode, root, root.getChildCount());

        TreePath path = new TreePath(newProjectNode.getPath());
        tree.scrollPathToVisible(path);
        tree.setSelectionPath(path);
    }
}
