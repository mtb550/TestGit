package testGit.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestCase;
import testGit.util.ActionHistory;

public class RedoDetails extends AnAction {
    TestCase tc;

    public RedoDetails(TestCase tc) {
        super("↪ Redo");
        this.tc = tc;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ActionHistory.redo();
    }
}
