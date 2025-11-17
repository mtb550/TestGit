package com.example.viewer;

import com.example.pojo.TestCase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.Consumer;

public class TestCaseToolWindow {
    public static void show(TestCase testCase) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("TestCaseDetails"); // in plugin.xml <toolWindow id="TestCaseDetails"

        if (toolWindow != null) {
            if (!toolWindow.isVisible()) {
                toolWindow.show();
            }

            TestCaseDetailsPanel viewer = TestCaseDetailsToolWindowFactory.getInstance();
            if (viewer != null) {
                viewer.update(testCase);
            }
        }
    }

    public static void addTestCase(Consumer<TestCase> onSaveCallback) {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("AddTestCase"); // in plugin.xml <toolWindow id="AddTestCase"

        if (toolWindow != null) {
            if (!toolWindow.isVisible()) {
                toolWindow.show();
            }

            AddTestCasePanel add = AddTestCaseToolWindowFactory.getInstance();
            if (add != null) {
                add.setOnSaveCallback(onSaveCallback);
            }
        }
    }
}
