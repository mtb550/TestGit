package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.dirs.TestRunDirectoryDto;
import testGit.util.reports.TestRunReport;

import javax.swing.tree.DefaultMutableTreeNode;

public class ReportExcel extends DumbAwareAction {
    private final SimpleTree tree;

    public ReportExcel(final SimpleTree tree) {
        super("As Excel", "Generate test run Excel report", AllIcons.Nodes.DataTables);
        this.tree = tree;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.getUserObject() instanceof TestRunDirectoryDto tr) {
            new TestRunReport(tr).build().asExcel();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        e.getPresentation().setEnabled(selectedNode != null && selectedNode.getUserObject() instanceof TestRunDirectoryDto);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }
}