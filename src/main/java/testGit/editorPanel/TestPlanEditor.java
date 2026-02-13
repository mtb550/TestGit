package testGit.editorPanel;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import testGit.pojo.Config;
import testGit.pojo.Directory;
import testGit.projectPanel.ProjectPanel;
import testGit.projectPanel.ProjectSelector;

import java.nio.file.Path;

public class TestPlanEditor {

    public static void open(final Path planPath, ProjectPanel projectPanel) {
        FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());

        Directory selectedProject = ProjectSelector.getSelectedProject();

        if (selectedProject == null) return;

        for (VirtualFile openFile : editorManager.getOpenFiles()) {
            if (openFile instanceof testGit.editorPanel.TestPlanVirtualFile existing &&
                    existing.getPlanPath().equals(planPath.toString())) {
                editorManager.openFile(existing, true);
                return;
            }
        }

        testGit.editorPanel.TestPlanVirtualFile virtualFile = new testGit.editorPanel.TestPlanVirtualFile(planPath.toString(), selectedProject);
        editorManager.openFile(virtualFile, true);
    }
}