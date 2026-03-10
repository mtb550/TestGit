package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class FileEditorImpl extends UserDataHolderBase implements FileEditor {

    private final JComponent component;
    private final VirtualFileImpl virtualFile;
    private final AutoCloseable uiDisposable;

    public FileEditorImpl(VirtualFileImpl vf) {
        this.virtualFile = vf;

        switch (vf.getEditorType()) {
            case TEST_RUN_CREATION -> {
                TestRunCreationUI creationUI = new TestRunCreationUI(vf.getTestCases());
                creationUI.setMetadata(vf.getMetadata());
                creationUI.setCurrentFile(vf);
                this.component = creationUI.createEditorPanel(vf.getTestCasesTreeModel(), vf.getRunPath(), vf.getProjectPanel());
                this.uiDisposable = creationUI::dispose;
            }
            case TEST_RUN_OPENING -> {
                TestRunOpeningUI openingUI = new TestRunOpeningUI(vf);
                this.component = openingUI.createEditorPanel();
                this.uiDisposable = openingUI::dispose;
            }
            default -> throw new IllegalArgumentException("Unsupported editor type: " + vf.getEditorType());
        }
    }

    @Override
    public @NotNull JComponent getComponent() {
        return component;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return component;
    }

    @Override
    public @NotNull String getName() {
        return "Test Run Editor";
    }

    @Override
    public @NotNull VirtualFile getFile() {
        return virtualFile;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void dispose() {
        try {
            uiDisposable.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener l) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener l) {
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }
}
