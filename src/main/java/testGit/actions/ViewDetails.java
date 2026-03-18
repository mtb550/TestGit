package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.mappers.TestCaseJsonMapper;
import testGit.viewPanel.ViewPanel;

public class ViewDetails extends DumbAwareAction {
    private final JBList<TestCaseJsonMapper> list;

    public ViewDetails(final JBList<TestCaseJsonMapper> list) {
        super("View Details", "", AllIcons.Actions.PreviewDetails);
        this.list = list;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        TestCaseJsonMapper tc = list.getSelectedValue();
        ViewPanel.show(tc);
    }
}
