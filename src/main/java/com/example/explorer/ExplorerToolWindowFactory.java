package com.example.explorer;

import com.example.explorer.actions.*;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExplorerToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ExplorerPanel panel = new ExplorerPanel();
        Content content = ContentFactory.getInstance().createContent(panel.getPanel(), "Display Name", false);
        toolWindow.getContentManager().addContent(content);
        toolWindow.setTitleActions(List.of(contextMenu(panel).getChildren(null)));
    }

    private DefaultActionGroup contextMenu(ExplorerPanel panel) {
        DefaultActionGroup group = new DefaultActionGroup();

        group.add(new ExpandAllAction(panel));
        group.add(new CollapseAllAction(panel));
        group.addSeparator();
        group.add(new RefreshAction(panel));
        group.add(new SettingsAction());
        group.add(new AddProjectAction(panel));

        return group;
    }
}