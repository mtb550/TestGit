package testGit.projectPanel;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPanelWithEmptyText;
import com.intellij.util.ui.StatusText;
import lombok.Getter;
import testGit.pojo.Config;
import testGit.projectPanel.projectSelector.TestProjectSelector;
import testGit.projectPanel.testCaseTab.TestCaseTabController;
import testGit.projectPanel.testRunTab.TestRunTabController;
import testGit.projectPanel.versionSelector.VersionSelector;

import java.awt.*;

@Getter
public class ProjectPanel implements Disposable {
    private final JBPanelWithEmptyText panel = new JBPanelWithEmptyText(new BorderLayout());
    private final TestProjectSelector testProjectSelector;
    private final TestCaseTabController testCaseTabController;
    private final TestRunTabController testRunTabController;
    private VersionSelector versionSelector;
    private Tabs tabs;

    public ProjectPanel(Project project) {
        System.out.println("ProjectPanel.ProjectPanel()");

        testProjectSelector = new TestProjectSelector(this);
        testCaseTabController = new TestCaseTabController(this);
        testRunTabController = new TestRunTabController(this);

        boolean status = testProjectSelector.init();

        if (status) {
            System.out.println("ProjectPanel(). projects found");

            panel.setLayout(new BorderLayout());
            JBPanel<?> topBar = new JBPanel<>(new BorderLayout());
            topBar.add(testProjectSelector.getSelectedTestProject(), BorderLayout.NORTH);

            versionSelector = new VersionSelector(testProjectSelector.getSelectedTestProject().getItem());
            topBar.add(versionSelector.getComponent(), BorderLayout.SOUTH);

            panel.add(topBar, BorderLayout.NORTH);

            tabs = new Tabs(project, this);
            panel.add(tabs.getComponent(), BorderLayout.CENTER);

            testCaseTabController.init();
            testRunTabController.init();

        } else {
            System.out.println("ProjectPanel(). not projects found");
            showEmptyState();
        }

    }

    public void setupMainLayout() {
        panel.removeAll();
        panel.getEmptyText().clear();

        boolean status = testProjectSelector.init();

        if (status) {
            System.out.println("ProjectPanel(). projects found");

            panel.setLayout(new BorderLayout());
            JBPanel<?> topBar = new JBPanel<>(new BorderLayout());
            topBar.add(testProjectSelector.getSelectedTestProject(), BorderLayout.NORTH);

            versionSelector = new VersionSelector(testProjectSelector.getSelectedTestProject().getItem());
            topBar.add(versionSelector.getComponent(), BorderLayout.SOUTH);

            panel.add(topBar, BorderLayout.NORTH);

            tabs = new Tabs(Config.getProject(), this);
            panel.add(tabs.getComponent(), BorderLayout.CENTER);

            testCaseTabController.init();
            testRunTabController.init();
        } else {
            System.out.println("ProjectPanel(). not projects found");
            showEmptyState();
        }

        panel.revalidate();
        panel.repaint();
    }

    public void init() {
        //testProjectSelector.loadTestProjectList();
        //testProjectSelector.init();
        //testCaseTabController.init();
        //testRunTabController.init();
        //this = new ProjectPanel(Config.getProject());
    }

    public void showEmptyState() {
        panel.removeAll();
        panel.getEmptyText().clear();
        StatusText emptyText = panel.getEmptyText();

        emptyText.clear();
        emptyText.setText("No test projects found.");
        emptyText.appendLine("Press ");
        emptyText.appendText("+ button", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        emptyText.appendText(" At the top panel");
        emptyText.appendLine("To create a new test project");

        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void dispose() {
        testCaseTabController.getTree().setModel(null);
        testRunTabController.getTree().setModel(null);
    }
}