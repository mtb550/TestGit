package testGit.projectPanel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBPanelWithEmptyText;
import com.intellij.util.ui.StatusText;
import lombok.Getter;
import testGit.actions.CreateTestProject;
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

            tabs = new Tabs(this);
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

            tabs = new Tabs(this);
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
        emptyText.setText("Welcome to QC plugin", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        emptyText.appendLine("");
        emptyText.appendLine("By", SimpleTextAttributes.GRAYED_ATTRIBUTES, null);
        emptyText.appendLine("Muteb Almughyiri", SimpleTextAttributes.GRAYED_ATTRIBUTES, null);
        emptyText.appendLine("");
        emptyText.appendLine("");
        emptyText.appendLine(AllIcons.General.Add, "", SimpleTextAttributes.LINK_ATTRIBUTES, null);
        emptyText.appendLine("Create your first new test project", SimpleTextAttributes.LINK_ATTRIBUTES, e -> {
            new CreateTestProject(this).actionPerformed(null);
        });
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void dispose() {
        testCaseTabController.getTree().setModel(null);
        testRunTabController.getTree().setModel(null);
    }
}