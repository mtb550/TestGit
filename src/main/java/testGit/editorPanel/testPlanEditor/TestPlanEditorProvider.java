package testGit.editorPanel.testPlanEditor;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class TestPlanEditorProvider implements FileEditorProvider {
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        // This MUST be true for the UI to load
        return file instanceof TestPlanVirtualFile;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new TestPlanFileEditor((TestPlanVirtualFile) file);
    }

    @Override
    public @NotNull String getEditorTypeId() {
        return "test-plan-checklist-editor";
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        // Hides the standard text editor so only your checklist shows
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
    }
}