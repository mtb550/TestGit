package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestCase;

public class GenerateTestCase extends AnAction {
    private final TestCase tc;

    public GenerateTestCase(TestCase tc) {
        super("Generate Test", "", AllIcons.RunConfigurations.TestState.Run);
        this.tc = tc;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        /// to be implemented
        System.out.println(tc.getTitle());
    }
}
