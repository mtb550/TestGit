package testGit.projectPanel.testCaseTab;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import testGit.actions.*;
import testGit.projectPanel.ProjectPanel;

@NoArgsConstructor
public class ContextMenu extends DefaultActionGroup {

    public ContextMenu(ProjectPanel projectPanel) {
        super("Test Case Context", true);

        SimpleTree tree = projectPanel.getTestCaseTree();

        add(new OpenFeature(tree));
        add(new AddGroup(tree));
        addSeparator();
        add(new Delete(projectPanel, tree));
        add(new Rename(projectPanel, tree));
        addSeparator();
        add(new Run(tree));
        addSeparator();
        add(createSubGroup("Export", AllIcons.ToolbarDecorator.Export, new ExportCsv(), new ExportHtml(), new ExportExcel(), new ExportJson()));
        add(createSubGroup("Import", AllIcons.ToolbarDecorator.Import, new ImportCsv(), new ImportExcel(), new ImportJson()));
        add(createSubGroup("Integrate", AllIcons.Nodes.Related, new IntegrateTestRail(), new IntegrateJira(), new IntegrateAzure()));
        addSeparator();
        add(new OpenOldVersions());
        add(new ViewCommits());
        add(new TestPlans());
    }

    /**
     * دالة مساعدة لإنشاء المجموعات الفرعية بأيقونات وبسطر واحد
     */
    private DefaultActionGroup createSubGroup(String title, javax.swing.Icon icon, com.intellij.openapi.actionSystem.AnAction... actions) {
        DefaultActionGroup group = new DefaultActionGroup(title, true);
        group.getTemplatePresentation().setIcon(icon);
        for (AnAction action : actions) {
            group.add(action);
        }
        return group;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT;
    }

    /**
     * كلاس داخلي لمجموعة "Add" للتحكم في الأيقونة وحالة الظهور (Validation)
     */
    private static class AddGroup extends DefaultActionGroup {

        public AddGroup(SimpleTree tree) {
            super("Create", "Create new items", AllIcons.General.Add);
            setPopup(true);

            add(new CreateModule(tree));
            add(new CreateTestSet(tree));
        }


    }
}