package testGit.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class IntegrateAzure extends AnAction {
    public IntegrateAzure() {
        super("From Azure DevOps");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // TODO: From Azure DevOps
    }
}
