package testGit.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class ImportJson extends AnAction {
    public ImportJson() {
        super("From Json");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // TODO: Import test cases From Json
    }
}
