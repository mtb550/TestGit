package testGit.editorPanel.testCaseEditor;

import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;
import testGit.pojo.Directory;
import testGit.pojo.TestCase;

import java.util.List;
import java.util.Objects;

@Getter
public class VirtualFileImpl extends LightVirtualFile {
    private final Directory dir;
    private final List<TestCase> testCases;

    public VirtualFileImpl(Directory dir, List<TestCase> testCases) {
        super(dir.getName(), FileType.INSTANCE, "");
        this.dir = dir;
        this.testCases = testCases;
        System.out.println("TestCaseVirtualFile.TestCaseVirtualFile()");
    }

    @Override
    public boolean equals(Object o) {
        // infinite sout
        //System.out.println("TestCaseVirtualFile.equals()");
        if (this == o) return true;
        if (!(o instanceof VirtualFileImpl that)) return false;
        // المقارنة الآن تعتمد على المسار لضمان عدم تكرار فتح نفس المجلد
        return Objects.equals(dir, that.dir);
    }

    @Override
    public int hashCode() {
        // infinite sout
        //System.out.println("TestCaseVirtualFile.hashCode()");
        return Objects.hash(dir);
    }

}