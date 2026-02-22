package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestCase;

public class UndoDetails extends AnAction {
    TestCase tc;

    public UndoDetails(TestCase tc) {
        super("Undo", "", AllIcons.Actions.Undo);
        this.tc = tc;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ///  TO BE IMPLEMENTED
    }
}
