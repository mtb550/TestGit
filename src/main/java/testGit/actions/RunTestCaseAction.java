package testGit.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Config;
import testGit.pojo.TestCase;
import testGit.util.Notifier;
import testGit.util.Runner.TestNGRunnerByClassName;
import testGit.util.Tools;

public class RunTestCaseAction extends AnAction {
    TestCase tc;

    public RunTestCaseAction(TestCase tc) {
        super("▶ Run Test");
        this.tc = tc;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String ref = tc.getAutomationRef();
        Project project = Config.getProject();
        if (ref != null && !ref.isBlank()) {
            Tools.printTestSourceRoots(project);
            TestNGRunnerByClassName.runTestClass(project, ref);
            Notifier.information("Running TestNG class: ", ref);
        } else {
            System.out.println("No automation reference found for this test case.");
            Notifier.warning("Note", "No automation reference found for this test case.");
        }
    }
}
