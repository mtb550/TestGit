package com.example.explorer;

import com.example.pojo.Tree;
import com.example.util.sql;
import lombok.Getter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class ExplorerTree {

    @Getter
    public static DefaultTreeModel treeModel;

    public static void build() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Test Cases");

        Tree[] rootNodes = new sql().get("SELECT * FROM tree WHERE type = 0").as(Tree[].class);

        for (Tree treeItem : rootNodes) {
            DefaultMutableTreeNode node = buildSubTree(treeItem);
            root.add(node);
        }

        treeModel = new DefaultTreeModel(root);
    }

    private static DefaultMutableTreeNode buildSubTree(Tree treeItem) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(treeItem);

        Tree[] children = new sql().get("SELECT * FROM tree WHERE link = ?", treeItem.getId()).as(Tree[].class);

        for (Tree childItem : children) {
            node.add(buildSubTree(childItem));
        }

        return node;
    }
}