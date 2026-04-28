package org.testin.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;

// TODO: implement save as to allow tester to specify save place
public class ExportJson extends DumbAwareAction {
    public ExportJson() {
        super("Export as Json", "", AllIcons.FileTypes.Json);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        /// TODO: Implement export logic to JSON
    }
}
