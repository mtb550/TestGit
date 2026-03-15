package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.DirectoryType;
import testGit.pojo.TestPackage;
import testGit.util.Tools;
import testGit.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

import static testGit.util.KeyboardSet.DeletePackage;

public class Remove extends DumbAwareAction {
    private final SimpleTree tree;

    public Remove(SimpleTree tree) {
        super("Remove", "Remove selected nodes", AllIcons.Actions.GC);
        this.tree = tree;
        this.registerCustomShortcutSet(DeletePackage.get(), tree);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TreePath[] paths = tree.getSelectionPaths();

        if (paths == null || paths.length == 0) return;

        List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

        for (TreePath path : paths) {
            if (path.getLastPathComponent() instanceof DefaultMutableTreeNode node &&
                    node.getUserObject() instanceof TestPackage pkg) {

                if (pkg.getType() != DirectoryType.PR &&
                        pkg.getType() != DirectoryType.TCP &&
                        pkg.getType() != DirectoryType.TRP) {
                    nodesToRemove.add(node);
                }
            }
        }

        if (nodesToRemove.isEmpty()) return;

        String message = nodesToRemove.size() == 1
                ? "Are you sure you want to remove '" + ((TestPackage) nodesToRemove.getFirst().getUserObject()).getName() + "'?"
                : "Are you sure you want to remove these " + nodesToRemove.size() + " items?";

        int confirm = Messages.showYesNoDialog(message, "Confirm Removing", Messages.getQuestionIcon());

        if (confirm == Messages.YES) {

            for (DefaultMutableTreeNode node : nodesToRemove) {
                TestPackage pkg = (TestPackage) node.getUserObject();

                if (pkg.getType() == DirectoryType.TS || pkg.getType() == DirectoryType.TR)
                    Tools.closeEditor(pkg.getName());

                TreeUtilImpl.removeVf(this, pkg.getFile());
                TreeUtilImpl.removeNode(node, tree);
            }
            System.out.println("Removed " + nodesToRemove.size() + " nodes.");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        TreePath[] paths = tree.getSelectionPaths();
        boolean canRemove = false;

        if (paths != null) {
            for (TreePath path : paths) {
                if (path.getLastPathComponent() instanceof DefaultMutableTreeNode node &&
                        node.getUserObject() instanceof TestPackage pkg) {

                    if (pkg.getType() != DirectoryType.PR &&
                            pkg.getType() != DirectoryType.TCP &&
                            pkg.getType() != DirectoryType.TRP) {
                        canRemove = true;
                        break;
                    }
                }
            }
        }

        e.getPresentation().setEnabled(canRemove);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}