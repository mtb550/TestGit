package org.testin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testin.editorPanel.UnifiedVirtualFile;
import org.testin.pojo.Config;
import org.testin.pojo.dto.dirs.TestSetDirectoryDto;

import java.util.ArrayList;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EditorUtil {

    private static final EditorUtil INSTANCE = new EditorUtil();

    public static EditorUtil getInstance() {
        return INSTANCE;
    }

    public boolean isEditorOpen(final String editorName) {
        FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());
        VirtualFile[] openFiles = editorManager.getOpenFiles();

        for (VirtualFile file : openFiles) {
            if (editorName.equals(file.getName())) {
                editorManager.openFile(file, true);
                return true;
            }
        }

        return false;
    }

    public void closeEditor(final String editorName) {
        FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());
        VirtualFile[] openFiles = editorManager.getOpenFiles();

        for (VirtualFile file : openFiles) {
            if (editorName.equals(file.getName())) {
                editorManager.closeFile(file);
                break;
            }
        }
    }

    public void closeThenOpenTestSetEditor(final VirtualFile targetDirectory, final TestSetDirectoryDto ts) {
        if (targetDirectory == null || ts == null) return;

        final Project project = Config.getProject();
        final FileEditorManager editorManager = FileEditorManager.getInstance(project);

        ApplicationManager.getApplication().invokeLater(() -> {
            VirtualFile fileToOpen = null;

            for (VirtualFile openFile : editorManager.getOpenFiles()) {
                if (openFile.getName().equals(targetDirectory.getName())) {
                    fileToOpen = openFile;
                    editorManager.closeFile(openFile);
                    break;
                }
            }

            if (fileToOpen == null) {
                openTestSetEditor(ts);
                return;
            }

            editorManager.openFile(fileToOpen, true);
        });
    }

    public void openTestSetEditor(final TestSetDirectoryDto ts) {
        final UnifiedVirtualFile newVirtualFile = new UnifiedVirtualFile(ts, new ArrayList<>());

        ApplicationManager.getApplication().invokeLater(() ->
                Optional.ofNullable(FileEditorManager.getInstance(Config.getProject()))
                        .ifPresent(editorManager -> editorManager.openFile(newVirtualFile, true))
        );
    }

    public void openTestSetEditorIfNotOpen(final TestSetDirectoryDto ts) {
        if (isEditorOpen(ts.getName())) {
            System.out.println("Editor already open, focusing: " + ts.getName());
        } else {
            System.out.println("Opening Test Set: " + ts.getPath());
            openTestSetEditor(ts);
        }
    }

}
