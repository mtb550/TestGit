package testGit.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.projectPanel.ProjectPanel;
import testGit.projectPanel.tree.ContextMenu;
import testGit.util.KeyboardSet;

import java.awt.*;

public class ShowTreeCM extends DumbAwareAction {
    private final ProjectPanel projectPanel;
    private final SimpleTree tree;

    public ShowTreeCM(ProjectPanel projectPanel, SimpleTree tree) {
        super("Show Context Menu");
        this.projectPanel = projectPanel;
        this.tree = tree;
        this.registerCustomShortcutSet(KeyboardSet.ContextMenu.getShortcut(), tree);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        int[] selectedRows = tree.getSelectionRows();
        if (selectedRows != null && selectedRows.length > 0) {
            Rectangle rect = tree.getRowBounds(selectedRows[0]);
            if (rect != null) {
                ContextMenu group = new ContextMenu(projectPanel);
                ActionManager.getInstance()
                        .createActionPopupMenu(ActionPlaces.TOOLWINDOW_POPUP, group)
                        .getComponent()
                        .show(tree, rect.x + (rect.width / 2), rect.y + (rect.height / 2));
            }
        }
    }
}