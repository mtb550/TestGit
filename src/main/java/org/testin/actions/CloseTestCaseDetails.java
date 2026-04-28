package org.testin.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;
import org.testin.util.KeyboardSet;
import org.testin.viewPanel.ViewPanel;
import org.testin.viewPanel.ViewToolWindowFactory;

import javax.swing.*;

public class CloseTestCaseDetails extends DumbAwareAction {

    public CloseTestCaseDetails(JComponent component) {
        super("Close View Panel");
        this.registerCustomShortcutSet(KeyboardSet.Escape.getCustomShortcut(), component);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ViewPanel viewer = ViewToolWindowFactory.getViewPanel();
        if (viewer != null) {
            viewer.hide().reset();
        }
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}