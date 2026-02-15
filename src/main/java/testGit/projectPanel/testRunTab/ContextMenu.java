package testGit.projectPanel.testRunTab;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.treeStructure.SimpleTree;
import testGit.actions.CreateTestRun;
import testGit.actions.CreateTestRunPackage;
import testGit.actions.Delete;
import testGit.actions.Rename;
import testGit.projectPanel.ProjectPanel;


public class ContextMenu extends DefaultActionGroup {

    public ContextMenu(ProjectPanel projectPanel) {
        super("Test Run Context", true);
        SimpleTree tree = projectPanel.getTestRunTree();

        add(new createGroup(tree, projectPanel));
        addSeparator();
        add(new Rename(projectPanel, tree));
        add(new Delete(projectPanel, tree));

    }

    /**
     * كلاس داخلي لفصل منطق "Add" والتحكم في حالته (Disabled vs Enabled)
     */
    private static class createGroup extends DefaultActionGroup {
        public createGroup(SimpleTree tree, ProjectPanel projectPanel) {
            super("Create", "Create test run items", AllIcons.General.Add);
            setPopup(true);
            add(new CreateTestRunPackage(tree));
            add(new CreateTestRun(tree, projectPanel));
        }


        /**
         * دالة مساعدة لإنشاء المجموعات الفرعية (في حال احتجت لإضافة Import/Export لاحقاً)
         */
        private DefaultActionGroup createSubGroup(String title, javax.swing.Icon icon, AnAction... actions) {
            DefaultActionGroup group = new DefaultActionGroup(title, true);
            group.getTemplatePresentation().setIcon(icon);
            for (AnAction action : actions) {
                group.add(action);
            }
            return group;
        }

    }

}