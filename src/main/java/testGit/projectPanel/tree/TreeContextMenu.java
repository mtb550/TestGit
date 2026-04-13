package testGit.projectPanel.tree;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import testGit.actions.*;
import testGit.projectPanel.ProjectPanel;

import javax.swing.*;

@NoArgsConstructor
public class TreeContextMenu extends DefaultActionGroup {

    public TreeContextMenu(final ProjectPanel projectPanel, final SimpleTree tree) {
        super("Tree Popup Menu", true);

        add(new Open(projectPanel, tree));
        add(new CreateTreeNode(projectPanel, tree));
        addSeparator();

        add(createSubGroup("Actions", AllIcons.Actions.Edit,
                new UndoNode(tree),
                new RedoNode(tree),
                new Remove(tree),
                new Rename(projectPanel, tree),
                new CopyNode(tree),
                new CutNode(tree),
                new PasteNode(tree)
        ));

        addSeparator();
        add(new RunTestSet(tree));
        addSeparator();

        add(createSubGroup("Export", AllIcons.ToolbarDecorator.Export,
                new ExportCsv(),
                new ExportHtml(),
                new ExportExcel(),
                new ExportJson()
        ));

        add(createSubGroup("Import", AllIcons.ToolbarDecorator.Import,
                new ImportCsv(),
                new ImportExcel(tree),
                new ImportJson()
        ));

        add(createSubGroup("Integrate", AllIcons.Nodes.Related,
                new IntegrateTestRail(),
                new IntegrateJira(),
                new IntegrateAzure()
        ));

        addSeparator();

        add(new OpenOldVersions());
        add(new ViewCommits());
        add(new TestRuns());
        addSeparator();

        add(createSubGroup("Generate Report", AllIcons.ToolbarDecorator.Export,
                new ReportHtml(tree),
                new ReportPdf(tree),
                new ReportExcel(tree)
        ));

    }

    public static void registerShortcuts(final SimpleTree tree, final TreeTransferHandler transferHandler, final TreeContextMenu treeContextMenu) {
        new Escape(tree, transferHandler);
        new OpenCM(tree, treeContextMenu);

    }

    private DefaultActionGroup createSubGroup(final String title, final Icon icon, final AnAction... actions) {
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

}