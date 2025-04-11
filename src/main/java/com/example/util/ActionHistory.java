package com.example.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;

import java.util.Stack;

public class ActionHistory {
    private static final Stack<Runnable> undoStack = new Stack<>();
    private static final Stack<Runnable> redoStack = new Stack<>();

    public static void registerUndo(Runnable undoAction) {
        undoStack.push(undoAction);
        redoStack.clear();
    }

    public static void undo() {
        if (!undoStack.isEmpty()) {
            Runnable action = undoStack.pop();
            action.run();
            redoStack.push(action);
        }
    }

    public static void redo() {
        if (!redoStack.isEmpty()) {
            Runnable action = redoStack.pop();
            action.run();
            undoStack.push(action);
        }
    }

    public static void showStatus(Project project) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        if (statusBar != null) {
            statusBar.setInfo("Undo: " + undoStack.size() + " | Redo: " + redoStack.size());
        }

        StatusUtil.showStatus(project, "Undo: " + undoStack.size() + " | Redo: " + redoStack.size());

    }

}
