package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestCase;
import testGit.viewPanel.ViewPanel;

public class ViewDetails extends AnAction {
    TestCase tc;

    public ViewDetails(TestCase tc) {
        super("View Details", "", AllIcons.Actions.PreviewDetails);
        this.tc = tc;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ViewPanel.show(tc);
    }
}
