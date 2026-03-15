package testGit.editorPanel.testRunEditor;

import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.EditorType;
import testGit.pojo.TestCase;
import testGit.pojo.TestPackage;
import testGit.pojo.TestRun;
import testGit.projectPanel.ProjectPanel;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;

@Getter
@Setter
public class VirtualFileImpl extends LightVirtualFile {
    private final ProjectPanel projectPanel;
    private final TestPackage pkg;
    private final DefaultTreeModel testCasesTreeModel;
    private final List<TestCase> testCases;
    private TestRun metadata;
    private EditorType editorType;

    public VirtualFileImpl(@NotNull TestPackage pkg, @NotNull DefaultTreeModel treeModel, List<TestCase> testCases, EditorType editorType, ProjectPanel projectPanel) {
        super(pkg.getName());
        this.pkg = pkg;
        this.testCasesTreeModel = treeModel;
        this.testCases = testCases;
        this.editorType = editorType;
        this.projectPanel = projectPanel;
    }

    @Override
    public boolean isValid() {
        return true;
    }
}