package org.testin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.util.KeyboardSet;

public class CutTestCaseNode extends DumbAwareAction {
    private final JBList<TestCaseDto> list;

    public CutTestCaseNode(final JBList<TestCaseDto> list) {
        super("Cut Node", "Copy selected test cases to clipboard", AllIcons.Actions.MenuCut);
        this.list = list;
        this.registerCustomShortcutSet(KeyboardSet.CutTestCaseNode.getCustomShortcut(), list);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

    }
}
