package testGit.projectPanel.testPlan;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import testGit.actions.AddTestPlanAction;
import testGit.actions.AddTestRunAction;
import testGit.actions.DeleteAction;
import testGit.actions.RenameAction;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;
import testGit.projectPanel.ProjectPanel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import static com.intellij.openapi.actionSystem.PlatformCoreDataKeys.CONTEXT_COMPONENT;

public class TestPlanContext extends DefaultActionGroup {

        public TestPlanContext(ProjectPanel projectPanel) {
            // نص المجموعة (يظهر إذا وضعت المجموعة داخل قائمة أخرى)
            super("Test Plan Context Menu", true);

            DefaultActionGroup addGroup = new DefaultActionGroup("➕ Add", true){
                @Override
                public void update(@NotNull AnActionEvent e) {
                    ///  find a way to replace it with getting tree from constructor.
                    SimpleTree tree = e.getData(CONTEXT_COMPONENT) instanceof SimpleTree simpleTree ? simpleTree : null;

                    boolean enabled = true;
                    if (tree != null) {
                        TreePath path = tree.getSelectionPath();
                        if (path != null) {
                            Object userObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                            if (userObject instanceof Directory treeItem && treeItem.getType() == DirectoryType.TR) {
                                enabled = false;
                            }
                        }
                    }

                    e.getPresentation().setEnabled(enabled);
                }

                @Override
                public @NotNull ActionUpdateThread getActionUpdateThread() {
                    // ضروري لأننا نلمس الشجرة (Swing Component)
                    return ActionUpdateThread.EDT;
                }
            };
            // 1. إضافة الأكشنز المرتبة
            addGroup.add(new AddTestPlanAction(projectPanel.getTestPlanTree()));
            addGroup.add(new AddTestRunAction(projectPanel.getTestPlanTree()));

            add(addGroup);
            addSeparator(); // فاصل بين العمليات الأساسية والإدارة

            add(new RenameAction(projectPanel, projectPanel.getTestPlanTree()));
            add(new DeleteAction(projectPanel, projectPanel.getTestPlanTree()));

            //addSeparator();

            //add(new TestPlanInfoAction(plan));
        }

    }

