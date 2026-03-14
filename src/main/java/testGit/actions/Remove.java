package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestPackage;
import testGit.pojo.TestProject;
import testGit.projectPanel.ProjectPanel;
import testGit.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;

import static testGit.util.KeyboardSet.DeletePackage;

public class Remove extends DumbAwareAction {
    private final SimpleTree tree;
    private final ProjectPanel projectPanel;

    public Remove(ProjectPanel projectPanel, SimpleTree tree) {
        super("Delete", "Delete selected node", AllIcons.Actions.GC);
        this.projectPanel = projectPanel;
        this.tree = tree;
        this.registerCustomShortcutSet(DeletePackage.get(), tree);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (node.getUserObject() instanceof TestProject pr) {

            int confirm = Messages.showYesNoDialog(
                    "Are you sure you want to remove '" + pr.getName() + "'?",
                    "Confirm",
                    Messages.getQuestionIcon()
            );

            if (confirm == Messages.YES) {
                TreeUtilImpl.removeVf(this, pr.getFile());

                System.out.println("remove project");
                projectPanel.getTestProjectSelector().removeTestProject(tree, pr);
            } else {
                System.out.println(node.getParent() == null);
                TreeUtilImpl.removeNode(node, tree);

            }

            return;
        }

        if (node.getUserObject() instanceof TestPackage pkg) {

        }

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        e.getPresentation().setEnabled(node != null && node.getUserObject() instanceof TestPackage);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}