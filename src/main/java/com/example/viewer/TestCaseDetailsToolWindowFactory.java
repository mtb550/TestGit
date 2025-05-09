package com.example.viewer;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class TestCaseDetailsToolWindowFactory implements ToolWindowFactory {
    @Getter
    private static TestCaseDetailsPanel instance;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        instance = new TestCaseDetailsPanel();
        ContentFactory contentFactory = ContentFactory.getInstance();

        Content detailsTab = contentFactory.createContent(instance.getDetailsPanel(), "Details", false);
        Content historyTab = contentFactory.createContent(instance.getHistoryPanel(), "History", false);
        Content bugsTab = contentFactory.createContent(instance.getBugPanel(), "Open Bugs", false);

        toolWindow.getContentManager().addContent(detailsTab);
        toolWindow.getContentManager().addContent(historyTab);
        toolWindow.getContentManager().addContent(bugsTab);

        // === F2 Shortcut Binding ===
        JComponent root = instance.getPanel();
        KeyStroke f2 = KeyStroke.getKeyStroke("F2");

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F2) {
                instance.toggleEditMode(true);
                return true;
            }
            return false;
        });

        root.getActionMap().put("editMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                instance.toggleEditMode(true); // enables editing
            }
        });
    }
}
