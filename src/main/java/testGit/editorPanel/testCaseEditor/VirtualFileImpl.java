package testGit.editorPanel.testCaseEditor;

import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestCase;
import testGit.pojo.TestPackage;

import java.util.List;

@Getter
public class VirtualFileImpl extends LightVirtualFile {
    private final TestPackage dir;
    private final List<TestCase> testCases;

    public VirtualFileImpl(@NotNull TestPackage dir, @NotNull List<TestCase> testCases) {
        super(dir.getName());
        this.dir = dir;
        this.testCases = testCases;
    }

    @Override
    public boolean isValid() {
        return true;
    }


}