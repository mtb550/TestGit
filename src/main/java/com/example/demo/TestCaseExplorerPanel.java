package com.example.demo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TestCaseExplorerPanel {
    private final JPanel panel;
    private final JTree tree;

    public TestCaseExplorerPanel() {
        panel = new JPanel(new BorderLayout());

        // Build root node
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Test Cases");

        List<Project> projects = DB.loadProjects();
        for (Project project : projects) {
            DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(project.getName());
            for (Feature feature : project.getFeatures()) {
                DefaultMutableTreeNode featureNode = new DefaultMutableTreeNode(new FeatureNodeData(project.getName(), feature));
                projectNode.add(featureNode);
            }
            root.add(projectNode);
        }

        tree = new JTree(root);
        JScrollPane scrollPane = new JScrollPane(tree);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Double-click to open feature in editor
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        Object userObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        if (userObject instanceof FeatureNodeData data) {
                            TestCaseEditor.open(data.projectName, data.feature);
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        tree.setSelectionPath(path);
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                        showContextMenu(e, selectedNode);
                    }
                }
            }
        });
    }

    private void showContextMenu(MouseEvent e, DefaultMutableTreeNode node) {
        JPopupMenu menu = new JPopupMenu();

        // ➕ Add options
        menu.add(new JMenuItem("➕ Add Suite"));
        menu.add(new JMenuItem("➕ Add Feature"));
        menu.addSeparator();

        // ❌ Delete
        menu.add(new JMenuItem("❌ Delete"));

        // ✏️ Rename
        menu.add(new JMenuItem("✏️ Rename"));
        menu.addSeparator();

        // ▶ Run Feature (only for Feature nodes)
        JMenuItem runItem = new JMenuItem("▶ Run Feature");
        runItem.setEnabled(node.getUserObject() instanceof FeatureNodeData);
        menu.add(runItem);
        menu.addSeparator();

        // 📤 Export submenu
        JMenu exportMenu = new JMenu("📤 Export");
        exportMenu.add(new JMenuItem("CSV"));
        exportMenu.add(new JMenuItem("HTML"));
        exportMenu.add(new JMenuItem("Excel"));
        menu.add(exportMenu);

        // 📥 Import
        menu.add(new JMenuItem("📥 Import"));
        menu.addSeparator();

        // 🕓 Old Versions
        menu.add(new JMenuItem("🕓 Open Old Versions"));

        // 📌 View Commits
        menu.add(new JMenuItem("📌 View Pending Commits"));

        menu.show(tree, e.getX(), e.getY());
    }

    public JPanel getPanel() {
        return panel;
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
