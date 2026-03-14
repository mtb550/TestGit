package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.DirectoryIcon;
import testGit.pojo.PackageType;
import testGit.pojo.TestPackage;
import testGit.pojo.TestProject;
import testGit.projectPanel.ProjectPanel;
import testGit.ui.CreateTestPackageDialog;
import testGit.ui.InputDialogList;
import testGit.util.Notifier;
import testGit.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.nio.file.Path;

public class CreateTestPackage extends DumbAwareAction {
    private final ProjectPanel projectPanel;
    private final SimpleTree tree;

    public CreateTestPackage(ProjectPanel projectPanel, SimpleTree tree) {
        super("New Package", "Create a new package", AllIcons.Nodes.Package);
        this.projectPanel = projectPanel;
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@Nullable AnActionEvent e) {
        System.out.println("CreateTestCasePackage.actionPerformed()");

        TreePath path = tree.getSelectionPath();
        if (path == null) {
            System.out.println("path is null !!, first case package");
            TestProject selectedTestProject = projectPanel.getTestProjectSelector().getSelectedTestProject().getItem();

//            InputDialog.show("Test Project Name", AllIcons.Nodes.Package, (enteredName) -> {
//                System.out.println("Processing: " + enteredName);
//                if (enteredName != null) {
//                    add_new(selectedTestProject, enteredName);
//                }
//            });

            InputDialogList.show("Test Project Name", (enteredName, selectedItem) -> {
                System.out.println("Processing: " + enteredName + " | Selected Type: " + selectedItem.name());
                if (enteredName != null && !enteredName.isEmpty()) {
                    add_new(selectedTestProject, enteredName);
                }
            });


            return;
        }

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = parentNode.getUserObject();

        if (!(userObject instanceof TestPackage treeItem) || treeItem.getPackageType() == PackageType.TS || treeItem.getPackageType() == PackageType.TR)
            return;

        String name = CreateTestPackageDialog.show();

        if (name == null || name.isBlank()) return;

        TestPackage newTestPackage = new TestPackage()
                .setPackageType(PackageType.PA)
                .setName(name);

        String folderName = String.format("%s_%s", newTestPackage.getPackageType().name(), newTestPackage.getName());
        Path fullPath = treeItem.getFilePath().resolve(folderName);

        newTestPackage.setFileName(folderName)
                .setFilePath(fullPath)
                .setFile(fullPath.toFile())
                .setIcon(DirectoryIcon.PA);

        TreeUtilImpl.insertVf(this, treeItem.getFilePath(), folderName);
        TreeUtilImpl.insertNode(tree, parentNode, newTestPackage);

    }

    /// to be removed
    private void add_new(TestProject selectedTestTestProject, String name) {
        Path parentPath = selectedTestTestProject.getFilePath().resolve("testCases");

        TestPackage newTestPackage = new TestPackage()
                .setPackageType(PackageType.PA)
                .setName(name);

        String folderName = String.format("%s_%s", newTestPackage.getPackageType().name(), newTestPackage.getName());
        Path fullPath = parentPath.resolve(folderName);

        newTestPackage.setFileName(folderName)
                .setFilePath(fullPath)
                .setFile(fullPath.toFile());

        TreeUtilImpl.insertVf(this, parentPath, folderName);
        TreeUtilImpl.insertNode(tree, projectPanel.getTestCaseTabController().getRootNode(), newTestPackage);
        Notifier.info("Test Package Created", "Create new test package under: " + parentPath);

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        TreePath path = tree.getSelectionPath();

        boolean shouldEnable = (path != null &&
                path.getLastPathComponent() instanceof DefaultMutableTreeNode node &&
                node.getUserObject() instanceof TestPackage item &&
                item.getPackageType() != PackageType.TS &&
                item.getPackageType() != PackageType.TR
        );

        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(shouldEnable);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}