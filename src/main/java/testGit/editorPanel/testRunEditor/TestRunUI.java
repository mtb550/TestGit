package testGit.editorPanel.testRunEditor;

import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBScrollPane;
import testGit.pojo.Directory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestRunUI {
    private CheckboxTree checklistTree;

    public JComponent createEditorPanel(DefaultTreeModel testCaseModel, String savePathString) {
        // 1. Convert your ready-made model nodes into CheckedTreeNodes
        CheckedTreeNode root = convertToCheckedNodes((DefaultMutableTreeNode) testCaseModel.getRoot());

        // 2. Initialize CheckboxTree
        checklistTree = new CheckboxTree(new CheckboxTree.CheckboxTreeCellRenderer() {
            @Override
            public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof CheckedTreeNode node && node.getUserObject() instanceof Directory dir) {
                    getTextRenderer().append(dir.getName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
                }
            }
        }, root);

        com.intellij.util.ui.tree.TreeUtil.expandAll(checklistTree);

        // 3. Layout with Save Button
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JBScrollPane(checklistTree), BorderLayout.CENTER);

        JButton saveButton = new JButton("Save Test Run");
        saveButton.addActionListener(e -> saveSelectedToJSON(root, savePathString));
        panel.add(saveButton, BorderLayout.SOUTH);

        return panel;
    }

    private void saveSelectedToJSON(CheckedTreeNode root, String savePath) {
        List<String> selectedFiles = new ArrayList<>();
        collectCheckedItems(root, selectedFiles);

        // Example: Print selected paths (You can use GSON here to write to the file)
        System.out.println("Saving to: " + savePath);
        for (String path : selectedFiles) {
            System.out.println("Included Test Case: " + path);
        }

        // TODO: Use your preferred JSON library to write 'selectedFiles' to 'savePath'
    }

    private void collectCheckedItems(CheckedTreeNode node, List<String> paths) {
        if (node.isChecked() && node.isLeaf() && node.getUserObject() instanceof Directory dir) {
            paths.add(dir.getFilePath().toString());
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectCheckedItems((CheckedTreeNode) node.getChildAt(i), paths);
        }
    }

    private CheckedTreeNode convertToCheckedNodes(DefaultMutableTreeNode node) {
        CheckedTreeNode newNode = new CheckedTreeNode(node.getUserObject());
        for (int i = 0; i < node.getChildCount(); i++) {
            newNode.add(convertToCheckedNodes((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return newNode;
    }
}