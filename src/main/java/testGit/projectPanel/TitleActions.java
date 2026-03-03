package testGit.projectPanel;

import com.intellij.openapi.actionSystem.AnAction;
import testGit.actions.*;

import java.util.List;

public class TitleActions {

    public static List<AnAction> create(ProjectPanel projectPanel) {
        return List.of(
                new OpenSettings(),
                new ExpandAll(projectPanel.getTestCaseTabController().getTree()),
                new CollapseAll(projectPanel.getTestRunTabController().getTree()),
                new Refresh(projectPanel),
                new CreateTestProject(projectPanel)
        );
    }
}