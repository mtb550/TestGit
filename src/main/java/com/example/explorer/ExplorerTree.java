package com.example.explorer;

import com.example.pojo.Config;
import com.example.pojo.Directory;
import lombok.Getter;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;

public class ExplorerTree {
    @Getter
    public static DefaultTreeModel treeModel;

    public static void buildTree() {
        System.out.println("ExplorerTree.buildTree()");
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("TEST CASES");
        File[] children = Config.getRootFolder().listFiles(File::isDirectory);

        if (children != null) {
            for (File child : children) {
                Directory project = mapProjectToDirectory(child);
                rootNode.add(buildSubTree(project));
            }
        }
        treeModel = new DefaultTreeModel(rootNode);
    }

    static DefaultMutableTreeNode buildSubTree(Directory folder) {
        System.out.println("ExplorerTree.buildSubTree()");

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
        File[] children = folder.getFilePath().toFile().listFiles(File::isDirectory);

        if (children != null) {
            for (File childFile : children) {
                Directory childTree = mapSuiteToDirectory(childFile);
                node.add(buildSubTree(childTree));
            }
        }
        return node;
    }

    public static Directory mapProjectToDirectory(File file) {
        System.out.println("ExplorerTree.mapProjectToDirectory()");

        String fileName = file.getName();
        String[] parts = fileName.split("_", 4);

        return new Directory()
                .setFile(file)
                .setFilePath(file.getAbsoluteFile().toPath())
                .setFileName(fileName)
                .setType(Integer.parseInt(parts[0]))
                .setId(Integer.parseInt(parts[1]))
                .setName(parts[2])
                .setActive(Integer.parseInt(parts[3]));
    }

    private static Directory mapSuiteToDirectory(File file) {
        System.out.println("ExplorerTree.mapSuiteToDirectory()");

        String fullName = file.getName();
        String[] parts = fullName.split("_", 3);

        return new Directory()
                .setFile(file)
                .setFilePath(file.getAbsoluteFile().toPath())
                .setFileName(file.getName())
                .setType(Integer.parseInt(parts[0]))
                .setId(Integer.parseInt(parts[1]))
                .setName(parts[2]);
    }

    private static class ReloadAllOnInsertListener implements TreeModelListener {
        @Override
        public void treeNodesChanged(TreeModelEvent e) {
            System.out.println("ExplorerTree.treeNodesChanged()");
        }

        @Override
        public void treeNodesInserted(TreeModelEvent e) {
            System.out.println("ExplorerTree.treeNodesInserted()");
            // NEVER call treeModel.reload() here; it causes the flicker.

            // Extract the actual file system path from the parent node
            //DefaultMutableTreeNode lastComponent = (DefaultMutableTreeNode) e.getTreePath().getLastPathComponent();
            //Directory dirNode = (Directory) lastComponent.getUserObject();
            // Use your Tools class to refresh the VFS for the parent folder
            //Tools.refreshFileSystem(dirNode.getFile());
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent e) {
            System.out.println("ExplorerTree.treeNodesRemoved()");

        }

        @Override
        public void treeStructureChanged(TreeModelEvent e) {
            System.out.println("ExplorerTree.treeStructureChanged()");

        }

    }
}