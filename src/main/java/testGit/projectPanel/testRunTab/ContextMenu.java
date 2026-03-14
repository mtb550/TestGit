package testGit.projectPanel.testRunTab;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.treeStructure.SimpleTree;
import testGit.actions.CreateTestRun;
import testGit.actions.CreateTestRunPackage;
import testGit.actions.Remove;
import testGit.actions.Rename;
import testGit.projectPanel.ProjectPanel;


public class ContextMenu extends DefaultActionGroup {

    public ContextMenu(ProjectPanel projectPanel) {
        super("Test Run Context", true);
        SimpleTree tree = projectPanel.getTestRunTabController().getTree();

        add(new createGroup(tree, projectPanel));
        addSeparator();
        add(new Rename(projectPanel, tree));
        add(new Remove(projectPanel, tree));

    }

    private static class createGroup extends DefaultActionGroup {
        public createGroup(SimpleTree tree, ProjectPanel projectPanel) {
            super("Create", "Create test run items", AllIcons.General.Add);
            setPopup(true);
            add(new CreateTestRunPackage(tree));
            add(new CreateTestRun(projectPanel));
        }

    }

}