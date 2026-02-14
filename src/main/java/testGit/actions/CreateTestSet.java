package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.editorPanel.testCaseEditor.TestCaseEditor;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;


public class CreateTestSet extends AnAction {
    private final SimpleTree tree;

    public CreateTestSet(final SimpleTree tree) {
        super("New Test Set", "Create a new test set", AllIcons.Nodes.Class);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("AddTestSet.actionPerformed()");
        if (tree == null) return;

        TreePath path = tree.getSelectionPath();
        if (path == null) return;

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = parentNode.getUserObject();

        if (!(userObject instanceof Directory treeItem) || treeItem.getType() == DirectoryType.F) return;

        String name = Messages.showInputDialog("Enter feature name:", "Add Feature", null);
        if (name == null || name.isBlank()) return;
        name = name.replace("_", " ");

        Directory newTestSet = new Directory()
                .setType(DirectoryType.F)
                .setName(name)
                .setActive(1);

        newTestSet.setFileName(String.format("%s_%s_%d", newTestSet.getType().toString().toLowerCase(), newTestSet.getName(), newTestSet.getActive()));
        newTestSet.setFilePath(treeItem.getFilePath().resolve(newTestSet.getFileName()));
        newTestSet.setFile(new File(newTestSet.getFileName()));

        WriteAction.run(() -> {
            try {
                VirtualFile parentDir = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(treeItem.getType() == DirectoryType.P ? treeItem.getFilePath().resolve("testCases") : treeItem.getFilePath());

                if (parentDir != null) {
                    parentDir.createChildDirectory(this, newTestSet.getFileName());

                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(newTestSet);
                    model.insertNodeInto(newNode, parentNode, parentNode.getChildCount());

                    tree.scrollPathToVisible(new TreePath(newNode.getPath()));
                    TestCaseEditor.open(newTestSet.getFilePath());
                }
            } catch (IOException ex) {
                System.err.println("unable to create test set: " + newTestSet);
            }
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        TreePath path = tree.getSelectionPath();

        boolean isFeature = (path != null &&
                path.getLastPathComponent() instanceof DefaultMutableTreeNode node &&
                node.getUserObject() instanceof Directory item &&
                item.getType() == DirectoryType.F);

        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(!isFeature);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}