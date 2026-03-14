package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.PackageType;
import testGit.pojo.TestPackage;
import testGit.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.nio.file.Path;

public class CreateTestRunPackage extends DumbAwareAction {
    private final SimpleTree tree;

    public CreateTestRunPackage(final SimpleTree tree) {
        super("New Package", "Create a new package", AllIcons.Nodes.Package);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@Nullable AnActionEvent e) {
        System.out.println("CreateTestRunPackage.actionPerformed()");

        TreePath path = tree.getSelectionPath();
        if (path == null) {
            System.out.println("path is null !!");
            return;
        }

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = parentNode.getUserObject();

        if (!(userObject instanceof TestPackage treeItem) || treeItem.getPackageType() == PackageType.TR) return;

        String name = Messages.showInputDialog("Enter package name:", "Create Package", AllIcons.Nodes.Package);
        if (name == null || name.isBlank()) return;
        name = name.replace("_", " ");

        Path parentPath = (treeItem.getPackageType() == PackageType.PR)
                ? treeItem.getFilePath().resolve("testRuns")
                : treeItem.getFilePath();

        TestPackage newTestPackage = new TestPackage()
                .setPackageType(PackageType.PA)
                .setName(name);

        String folderName = String.format("%s_%s", newTestPackage.getPackageType().name(), newTestPackage.getName());
        Path fullPath = parentPath.resolve(folderName);

        newTestPackage.setFileName(folderName)
                .setFilePath(fullPath)
                .setFile(fullPath.toFile());

        TreeUtilImpl.insertVf(this, parentPath, folderName);
        TreeUtilImpl.insertNode(tree, parentNode, newTestPackage);

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        TreePath path = tree.getSelectionPath();

        boolean isTestRun = (path != null &&
                path.getLastPathComponent() instanceof DefaultMutableTreeNode node &&
                node.getUserObject() instanceof TestPackage item &&
                (item.getPackageType() == PackageType.PA || item.getPackageType() == PackageType.TR));

        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(!isTestRun);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}
