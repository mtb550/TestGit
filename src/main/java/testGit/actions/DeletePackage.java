package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Directory;
import testGit.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;

import static testGit.util.KeyboardSet.DeletePackage;

public class DeletePackage extends DumbAwareAction {
    private final SimpleTree tree;

    public DeletePackage(final SimpleTree tree) {
        super("Delete", "Delete selected node", AllIcons.Actions.GC);
        this.tree = tree;
        this.registerCustomShortcutSet(DeletePackage.get(), tree);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (!(node.getUserObject() instanceof Directory treeItem)) return;

        int confirm = Messages.showYesNoDialog(
                "Are you sure you want to delete '" + treeItem.getName() + "'?",
                "Confirm Delete",
                Messages.getQuestionIcon()
        );

        if (confirm == Messages.YES) {
            TreeUtilImpl.removeVf(this, treeItem.getFile());
            TreeUtilImpl.removeNode(node, tree);
        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        e.getPresentation().setEnabled(node != null && node.getUserObject() instanceof Directory);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}