package com.example.explorer.actions;

import com.example.pojo.Directory;
import com.example.util.NodeType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.intellij.openapi.actionSystem.PlatformCoreDataKeys.CONTEXT_COMPONENT;

public class AddSuiteAction extends AnAction {
    public AddSuiteAction() {
        super("➕ New Suite");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JTree tree = e.getData(CONTEXT_COMPONENT) instanceof JTree jTree ? jTree : null;
        if (tree == null) return;

        TreePath path = tree.getSelectionPath();
        if (path == null) return;

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = parentNode.getUserObject();

        if (!(userObject instanceof Directory treeItem) || treeItem.getType() == NodeType.FEATURE.getCode()) return;

        String name = Messages.showInputDialog("Enter suite name:", "Add Suite", null);
        if (name == null || name.isBlank()) return;

        // دمج المسار الأساسي مع اسم المجلد الجديد
        Path fullPath = treeItem.getFilePath().resolve(name);

        // Build new node and insert it
        String[] parts = name.split("_", 4);
        Directory newSuite = new Directory()
                .setType(NodeType.SUITE.getCode())
                .setId(Integer.parseInt(parts[1]))
                .setName(parts[2])
                .setFileName(name)
                .setFile(new File(name))
                .setFilePath(fullPath);

        try {
            Files.createDirectories(fullPath);
            System.out.println("Success! Path created: " + fullPath);
        } catch (IOException ee) {
            System.err.println("Could not create folder: " + ee.getMessage());
        }

        DefaultMutableTreeNode newSuiteNode = new DefaultMutableTreeNode(newSuite);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.insertNodeInto(newSuiteNode, parentNode, parentNode.getChildCount());

        tree.scrollPathToVisible(new TreePath(newSuiteNode.getPath()));
    }
}
