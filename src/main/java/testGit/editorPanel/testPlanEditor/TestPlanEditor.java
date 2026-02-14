package testGit.editorPanel.testPlanEditor;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import testGit.pojo.Config;
import testGit.projectPanel.ProjectPanel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.nio.file.Path;

public class TestPlanEditor {

    public static void open(final Path planPath, ProjectPanel projectPanel, DefaultMutableTreeNode selectedNode) {
        FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());

        // Check if the file is already open to avoid duplicates
        for (VirtualFile openFile : editorManager.getOpenFiles()) {
            if (openFile instanceof TestPlanVirtualFile existing &&
                    existing.getPlanPath().equals(planPath.toString())) {
                editorManager.openFile(existing, true);
                return;
            }
        }

        // Use the model directly from your projectPanel as requested
        TestPlanVirtualFile virtualFile = new TestPlanVirtualFile(
                planPath.toString(),
                (DefaultTreeModel) projectPanel.getTestCaseTree().getModel()
        );

        editorManager.openFile(virtualFile, true);
    }
}