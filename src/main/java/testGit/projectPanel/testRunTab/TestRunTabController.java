package testGit.projectPanel.testRunTab;

import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;
import testGit.projectPanel.ProjectPanel;
import testGit.util.TestRunsDirectoryMapper;

public class TestRunTabController {
    private final ProjectPanel projectPanel;
    @Getter
    private final SimpleTree tree;


    public TestRunTabController(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.tree = new SimpleTree();
    }

    public void init() {
        tree.setCellRenderer(new TestRunRenderer());
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        tree.addMouseListener(new MouseAdapterImpl(projectPanel));
        ShortcutHandler.register(tree);

        TestRunsDirectoryMapper.buildTreeAsync(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem(), tree);
    }
}