package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.fileEditor.FileEditorManager;
import testGit.pojo.Config;
import testGit.projectPanel.ProjectPanel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.nio.file.Path;

public class TestRunEditor {

    public static void open(final Path runPath, ProjectPanel projectPanel, DefaultMutableTreeNode selectedNode) {
        FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());

        for (com.intellij.openapi.vfs.VirtualFile openFile : editorManager.getOpenFiles()) {
            if (openFile instanceof VirtualFileImpl existing &&
                    existing.getRunPath().equals(runPath.toString())) {
                editorManager.openFile(existing, true);
                return;
            }
        }

        VirtualFileImpl virtualFile = new VirtualFileImpl(
                runPath.toString(),
                (DefaultTreeModel) projectPanel.getTestCaseTree().getModel()
        );

        editorManager.openFile(virtualFile, true);
    }
}