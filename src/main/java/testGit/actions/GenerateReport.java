package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.dirs.TestRunDirectoryDto;
import testGit.util.reports.TestRunReportGenerator;

import javax.swing.tree.DefaultMutableTreeNode;

public class GenerateReport extends DumbAwareAction {
    private final SimpleTree tree;

    public GenerateReport(final SimpleTree tree) {
        super("Generate Report", "Generate test run HTML report", AllIcons.ToolbarDecorator.Export);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            new TestRunReportGenerator().generateFromNode(selectedNode);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        boolean isTestRun = selectedNode != null && selectedNode.getUserObject() instanceof TestRunDirectoryDto;
        e.getPresentation().setEnabled(isTestRun);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}