package com.example.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class AddTestCaseToolWindowFactory implements ToolWindowFactory {
    @Getter
    private static AddTestCasePanel instance;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        instance = new AddTestCasePanel();
        ContentFactory contentFactory = ContentFactory.getInstance();

        Content content = contentFactory.createContent(instance.getPanel(), "Add Test Case", false);
        toolWindow.getContentManager().addContent(content);
    }
}