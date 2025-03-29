package com.example.demo;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import org.jetbrains.annotations.NotNull;

public class TestCaseToolWindowFactory implements ToolWindowFactory {
    private static TestCaseToolWindowContent contentInstance;

    public static TestCaseToolWindowContent getInstance() {
        return contentInstance;
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        contentInstance = new TestCaseToolWindowContent();
        Content content = com.intellij.ui.content.ContentFactory.getInstance()
                .createContent(contentInstance.getPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}