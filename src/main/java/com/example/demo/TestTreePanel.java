package com.example.demo;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.List;

public class TestTreePanel {
    private JPanel mainPanel;
    private JTree tree;

    public TestTreePanel() {
        mainPanel = new JPanel(new BorderLayout());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Test Projects");
        List<Project> projects = DB.loadProjects();
        for (Project project : projects) {
            DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project.getName());
            for (Feature feature : project.getFeatures()) {
                projectNode.add(new DefaultMutableTreeNode(new FeatureNodeData(project.getName(), feature)));
            }
            root.add(projectNode);
        }

        tree = new JTree(root);
        JScrollPane scrollPane = new JScrollPane(tree);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selected == null || !(selected.getUserObject() instanceof FeatureNodeData)) return;

            FeatureNodeData data = (FeatureNodeData) selected.getUserObject();
            SwingUtilities.invokeLater(() -> TestCaseEditor.open(data.projectName, data.feature));
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    static class FeatureNodeData {
        String projectName;
        Feature feature;

        FeatureNodeData(String projectName, Feature feature) {
            this.projectName = projectName;
            this.feature = feature;
        }

        public String toString() {
            return feature.getName();
        }
    }
}
