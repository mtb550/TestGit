package testGit.projectPanel.testCaseTab;

import com.intellij.icons.AllIcons;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import testGit.pojo.Directory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.Set;

/**
 * Custom TreeCellRenderer for IntelliJ UI components.
 * Separates icon logic and text styling for better maintainability.
 */
public class TestCaseRenderer extends SimpleColoredComponent implements TreeCellRenderer {
    private final Set<DefaultMutableTreeNode> cutNodes;

    public TestCaseRenderer(Set<DefaultMutableTreeNode> cutNodes) {
        this.cutNodes = cutNodes;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        this.clear();
        if (value instanceof DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();
            if (userObject instanceof Directory dir) {
                renderDirectory(node, dir, selected);
            } else {
                setIcon(AllIcons.Nodes.Unknown);
                append(value.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
            }
        }
        return this;
    }

    private void renderDirectory(DefaultMutableTreeNode node, Directory dir, boolean selected) {
        setIcon(getIconForDirectory(dir));

        // Check the shared set to see if this specific node instance is "cut"
        SimpleTextAttributes style = cutNodes.contains(node)
                ? SimpleTextAttributes.GRAYED_ATTRIBUTES
                : SimpleTextAttributes.REGULAR_ATTRIBUTES;

        append(dir.getName() != null ? dir.getName() : "Unnamed", style);
    }

    private Icon getIconForDirectory(Directory dir) {
        return switch (dir.getType()) {
            case PR -> AllIcons.Nodes.Project;
            case PA -> AllIcons.Nodes.WebFolder;
            case TS -> AllIcons.Nodes.Class;
            default -> AllIcons.Nodes.Unknown;
        };
    }
}