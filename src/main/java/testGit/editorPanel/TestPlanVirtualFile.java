package testGit.editorPanel;

import com.intellij.testFramework.LightVirtualFile;
import testGit.pojo.Directory;

public class TestPlanVirtualFile extends LightVirtualFile {
    private final String planPath;
    private final Directory selectedProject;

    public TestPlanVirtualFile(String planPath, Directory selectedProject) {
        super("New Test Run Setup");
        this.planPath = planPath;
        this.selectedProject = selectedProject;
    }

    public String getPlanPath() {
        return planPath;
    }

    public Directory getSelectedProject() {
        return selectedProject;
    }
}