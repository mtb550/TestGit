package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.DirectoryType;
import testGit.pojo.TestPackage;
import testGit.pojo.TestProject;
import testGit.projectPanel.ProjectPanel;
import testGit.ui.InputDialogList_TestRun;
import testGit.util.Notifier;
import testGit.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.nio.file.Path;

public class CreateTestRunItems extends DumbAwareAction {
    private final SimpleTree tree;
    private final ProjectPanel projectPanel;

    public CreateTestRunItems(ProjectPanel projectPanel, SimpleTree tree) {
        super("New Item", "Create a new item", AllIcons.Nodes.Package);
        this.projectPanel = projectPanel;
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@Nullable AnActionEvent e) {
        System.out.println("CreateTestRunItems.actionPerformed()");

        TreePath path = tree.getSelectionPath();
        if (path == null) {
            System.out.println("path is null !!");

            TestProject selectedTestTestProject = projectPanel.getTestProjectSelector().getSelectedTestProject().getItem();


            InputDialogList_TestRun.show("Test Project Name", (enteredName, selectedItem) -> {
                System.out.println("Processing: " + enteredName + " | Selected Type: " + selectedItem.name());
                if (enteredName != null && !enteredName.isEmpty()) {
                    add_new(selectedTestTestProject, enteredName, selectedItem);
                }
            });

            return;
        }

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = parentNode.getUserObject();

        if (!(userObject instanceof TestPackage treeItem) || treeItem.getDirectoryType() == DirectoryType.TR) return;

        String name = Messages.showInputDialog("Enter package name:", "Create Package", AllIcons.Nodes.Package);
        if (name == null || name.isBlank()) return;
        name = name.replace("_", " ");

        Path parentPath = (treeItem.getDirectoryType() == DirectoryType.PR)
                ? treeItem.getFilePath().resolve("testRuns")
                : treeItem.getFilePath();

        TestPackage newTestPackage = new TestPackage()
                .setDirectoryType(DirectoryType.PA)
                .setName(name);

        String folderName = String.format("%s_%s_%s", newTestPackage.getDirectoryType().name(), newTestPackage.getName());
        Path fullPath = parentPath.resolve(folderName);

        newTestPackage.setFileName(folderName)
                .setFilePath(fullPath)
                .setFile(fullPath.toFile());

        TreeUtilImpl.insertVf(this, parentPath, folderName);
        TreeUtilImpl.insertNode(tree, parentNode, newTestPackage);

    }

    private void add_new(TestProject selectedTestTestProject, String enteredName, InputDialogList_TestRun.TemplateItem selectedItem) {
        Path parentPath = selectedTestTestProject.getFilePath().resolve("testRuns");

        if (selectedItem.directoryType() == DirectoryType.PA) {
            TestPackage newTestPackage = new TestPackage()
                    .setDirectoryType(DirectoryType.PA)
                    .setName(enteredName);

            String folderName = String.format("%s_%s_%s", newTestPackage.getDirectoryType().name(), newTestPackage.getName());
            Path fullPath = parentPath.resolve(folderName);

            newTestPackage.setFileName(folderName)
                    .setFilePath(fullPath)
                    .setFile(fullPath.toFile());

            TreeUtilImpl.insertVf(this, parentPath, folderName);
            TreeUtilImpl.insertNode(tree, projectPanel.getTestCaseTabController().getRootNode(), newTestPackage);
            Notifier.info("Test Package Created", "Create new test package under: " + parentPath);
        } else if (selectedItem.directoryType() == DirectoryType.TR) {
            TestPackage newTestRun = new TestPackage()
                    .setDirectoryType(DirectoryType.TR)
                    .setName(enteredName);

            String folderName = String.format("%s_%s_%s", newTestRun.getDirectoryType().name(), newTestRun.getName());
            Path fullPath = parentPath.resolve(folderName);

            newTestRun.setFileName(folderName)
                    .setFilePath(fullPath)
                    .setFile(fullPath.toFile());

            TreeUtilImpl.insertVf(this, parentPath, folderName);
            TreeUtilImpl.insertNode(tree, projectPanel.getTestCaseTabController().getRootNode(), newTestRun);
            Notifier.info("Test Run Created", "Create new test run under: " + parentPath);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        TreePath path = tree.getSelectionPath();

        boolean isTestRun = (path != null &&
                path.getLastPathComponent() instanceof DefaultMutableTreeNode node &&
                node.getUserObject() instanceof TestPackage item &&
                (item.getDirectoryType() == DirectoryType.PA || item.getDirectoryType() == DirectoryType.TR));

        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(!isTestRun);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}
