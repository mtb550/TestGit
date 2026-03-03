package testGit.projectPanel;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import lombok.Getter;
import testGit.projectPanel.projectSelector.TestProjectSelector;
import testGit.projectPanel.testCaseTab.TestCaseTabController;
import testGit.projectPanel.testRunTab.TestRunTabController;
import testGit.projectPanel.versionSelector.VersionSelector;

import java.awt.*;

@Getter
public class ProjectPanel implements Disposable {
    private final JBPanel<?> panel;
    private final TestProjectSelector testProjectSelector;
    private final VersionSelector versionSelector;
    private final Tabs tabs;
    private final TestCaseTabController testCaseTabController;
    private final TestRunTabController testRunTabController;

    public ProjectPanel(Project project) {
        panel = new JBPanel<>(new BorderLayout());

        testProjectSelector = new TestProjectSelector(this);

        testCaseTabController = new TestCaseTabController(this);
        testRunTabController = new TestRunTabController(this);

        JBPanel<?> topBar = new JBPanel<>(new BorderLayout());
        topBar.add(testProjectSelector.getSelectedTestProject(), BorderLayout.NORTH);

        versionSelector = new VersionSelector(testProjectSelector.getSelectedTestProject().getItem());
        topBar.add(versionSelector.getComponent(), BorderLayout.SOUTH);

        panel.add(topBar, BorderLayout.NORTH);

        tabs = new Tabs(project, this);
        panel.add(tabs.getComponent(), BorderLayout.CENTER);
    }

    public void init() {
        //testProjectSelector.loadTestProjectList();
        testProjectSelector.init();
        testCaseTabController.init();
        testRunTabController.init();
    }

    @Override
    public void dispose() {
        testCaseTabController.getTree().setModel(null);
        testRunTabController.getTree().setModel(null);
    }
}