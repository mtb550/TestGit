package testGit.projectPanel.testRunTab;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;
import testGit.projectPanel.ProjectPanel;
import testGit.projectPanel.projectSelector.ProjectSelector;
import testGit.util.TestRunsDirectoryMapper;

public class TestRunTabController {
    @Getter
    private final SimpleTree tree;
    private final ProjectPanel projectPanel;

    public TestRunTabController(ProjectPanel projectPanel) {
        this.tree = new SimpleTree();
        this.projectPanel = projectPanel;
    }

    public void setup(Project project) {
        if (ProjectSelector.getSelectedProject() == null) {
            tree.getEmptyText().setText("Select a project to view test runs.");
            return;
        }

        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new TestRunRenderer());
        tree.addMouseListener(new MouseAdapterImpl(projectPanel));
        ShortcutHandler.register(tree);

        DumbService.getInstance(project).runWhenSmart(() ->
                TestRunsDirectoryMapper.buildTreeAsync(tree)
        );
    }
}