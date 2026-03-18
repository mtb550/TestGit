package testGit.editorPanel;

import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;
import lombok.Setter;
import testGit.pojo.Directory;
import testGit.pojo.EditorType;
import testGit.pojo.TestRun;
import testGit.pojo.TestSet;
import testGit.pojo.mappers.TestCaseJsonMapper;
import testGit.pojo.mappers.TestRunJsonMapper;
import testGit.projectPanel.ProjectPanel;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;

@Getter
@Setter
public class UnifiedVirtualFile extends LightVirtualFile {

    // --- خصائص مشتركة (Shared Properties) ---
    private final Directory directory; // يمكن أن يكون TestSet أو TestRun
    private final List<TestCaseJsonMapper> testCaseJsonMappers;

    // --- خصائص خاصة بـ Test Run (ستكون null في حالة Test Case) ---
    private ProjectPanel projectPanel;
    private DefaultTreeModel testCasesTreeModel;
    private TestRunJsonMapper metadata;
    private EditorType editorType;

    // 🌟 1. المُنشئ الخاص بـ Test Case
    public UnifiedVirtualFile(TestSet directory, List<TestCaseJsonMapper> testCaseJsonMappers) {
        super(directory.getName());
        this.directory = directory;
        this.testCaseJsonMappers = testCaseJsonMappers;
        this.setFileType(FileType.TEST_CASE);
    }

    // 🌟 2. المُنشئ الخاص بـ Test Run
    public UnifiedVirtualFile(TestRun directory, DefaultTreeModel treeModel, List<TestCaseJsonMapper> testCaseJsonMappers, EditorType editorType, ProjectPanel projectPanel) {
        super(directory.getName());
        this.directory = directory;
        this.testCaseJsonMappers = testCaseJsonMappers;
        this.testCasesTreeModel = treeModel;
        this.editorType = editorType;
        this.projectPanel = projectPanel;
        this.setFileType(FileType.TEST_RUN);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    // --- دوال مساعدة لجلب الكائن بالنوع الصحيح بسهولة ---

    public TestSet getTestSet() {
        return directory instanceof TestSet ? (TestSet) directory : null;
    }

    public TestRun getTestRunPkg() {
        return directory instanceof TestRun ? (TestRun) directory : null;
    }
}