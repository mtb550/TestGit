package com.example.explorer.testPlan;

import com.example.pojo.TestCase;
import com.example.pojo.TestPlan;
import com.example.pojo.Tree;
import com.example.util.NodeType;
import com.example.util.sql;
import com.intellij.ui.CheckedTreeNode;

import java.util.ArrayList;
import java.util.List;

public class TestPlanTreeBuilder {
    private final TestPlan plan;
    private final List<CheckedTreeNode> allTestCaseNodes = new ArrayList<>();

    public TestPlanTreeBuilder(TestPlan plan) {
        this.plan = plan;
    }

    public CheckedTreeNode buildTree() {
        CheckedTreeNode rootNode = new CheckedTreeNode("Test Cases");
        Tree root = new sql().get("SELECT * FROM tree WHERE id = ?", plan.getProject_id()).as(Tree.class);
        if (root != null) {
            buildTreeRecursive(root, rootNode);
        }
        return rootNode;
    }

    private void buildTreeRecursive(Tree treeItem, CheckedTreeNode parentNode) {
        CheckedTreeNode currentNode = new CheckedTreeNode(treeItem);
        currentNode.setChecked(false);
        parentNode.add(currentNode);

        if (treeItem.getType() == NodeType.FEATURE.getCode()) {
            TestCase[] testCases = new sql().get("SELECT * FROM nafath_tc WHERE module = ? ORDER BY sort", treeItem.getId()).as(TestCase[].class);
            for (TestCase tc : testCases) {
                CheckedTreeNode testCaseNode = new CheckedTreeNode(tc);
                testCaseNode.setChecked(false);
                allTestCaseNodes.add(testCaseNode);
                currentNode.add(testCaseNode);
            }
            return;
        }

        Tree[] children = new sql().get("SELECT * FROM tree WHERE link = ?", treeItem.getId()).as(Tree[].class);
        for (Tree child : children) {
            buildTreeRecursive(child, currentNode);
        }
    }
}