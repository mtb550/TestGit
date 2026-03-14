package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.editorPanel.testRunEditor.TestRunEditor;
import testGit.pojo.DirectoryType;
import testGit.pojo.Package;
import testGit.pojo.TestRun;
import testGit.pojo.TestRunStatus;
import testGit.projectPanel.ProjectPanel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class CreateTestRun extends DumbAwareAction {
    private final ProjectPanel projectPanel;
    private final SimpleTree testRunTree;

    public CreateTestRun(ProjectPanel projectPanel) {
        super("New Test Run", "Create a new test run", AllIcons.Actions.GroupBy);
        this.projectPanel = projectPanel;
        this.testRunTree = projectPanel.getTestRunTabController().getTree();
    }

    @Override
    public void actionPerformed(@Nullable AnActionEvent e) {
        TreePath path = testRunTree.getSelectionPath();
        if (path == null) {
            System.out.println("path is null !!");
            return;
        }

        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = parentNode.getUserObject();

        if (!(userObject instanceof Package treeItem) || treeItem.getDirectoryType() == DirectoryType.TR) {
            System.out.println("!(userObject instanceof Directory treeItem) || treeItem.getType() == DirectoryType.TR");
            return;
        }

        /*TestRunEditor.open(treeItem.getFilePath(), projectPanel, parentNode);*/
        //NewTestRunDialog dialog = new NewTestRunDialog();
        //if (dialog.showAndGet()) {
        //TestRun metadata = dialog.getMetadata();

        TestRun metadata = new TestRun();
        metadata.setStatus(TestRunStatus.CREATED);

        TestRunEditor.create(
                treeItem.getFilePath(),
                projectPanel,
                projectPanel.getTestProjectSelector().getSelectedTestProject().getItem(),
                metadata
        );
        //}
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        TreePath path = testRunTree.getSelectionPath();

        boolean isTestRun = (path != null &&
                path.getLastPathComponent() instanceof DefaultMutableTreeNode node &&
                node.getUserObject() instanceof Package item &&
                item.getDirectoryType() == DirectoryType.TR);

        e.getPresentation().setVisible(true);
        e.getPresentation().setEnabled(!isTestRun);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

}