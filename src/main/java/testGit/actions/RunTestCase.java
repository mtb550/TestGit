package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.TestCaseDto;
import testGit.util.KeyboardSet;
import testGit.util.Tools;
import testGit.util.notifications.Notifier;
import testGit.util.runner.TestNGRunnerByMethod;

import java.util.List;

public class RunTestCase extends DumbAwareAction {
    private final JBList<TestCaseDto> list;

    public RunTestCase(final JBList<TestCaseDto> list) {
        super("Run Test", "Run selected test cases", AllIcons.RunConfigurations.TestState.Run);
        this.list = list;
        this.registerCustomShortcutSet(KeyboardSet.RunTestCase.getCustomShortcut(), list);
    }

    public void execute(final @NotNull List<TestCaseDto> testCases) {
        if (testCases.isEmpty()) return;

        for (TestCaseDto tc : testCases) {
            if (tc == null || "RUNNING".equals(tc.getTempStatus())) continue;

            Notifier.showCustomBottomRightBalloon("Running Test Case: ", tc.getDescription());
            TestNGRunnerByMethod.runTestMethod(tc.getFqcn(), Tools.toCamelCase(tc.getDescription()));
        }
    }

    public void execute(final @NotNull TestCaseDto tc) {
        execute(List.of(tc));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TestCaseDto> selectedValues = list.getSelectedValuesList();
        execute(selectedValues);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}