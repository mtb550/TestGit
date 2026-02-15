package testGit.editorPanel.testRunEditor;

import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;

import javax.swing.tree.DefaultTreeModel;
import java.nio.file.Paths;

@Getter
public class VirtualFileImpl extends LightVirtualFile {
    private final String runPath;
    private final DefaultTreeModel testCasesTreeModel;

    public VirtualFileImpl(String runPath, DefaultTreeModel testCasesTreeModel) {
        super("Test Run: " + Paths.get(runPath).getFileName().toString());
        this.runPath = runPath;
        this.testCasesTreeModel = testCasesTreeModel;
    }
}