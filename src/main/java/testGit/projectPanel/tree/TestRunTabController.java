package testGit.projectPanel.tree;

import com.intellij.openapi.application.ApplicationManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestPackage;
import testGit.pojo.TestProject;
import testGit.projectPanel.ProjectPanel;
import testGit.util.DirectoryMapper;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TestRunTabController {
    private final ProjectPanel projectPanel;
    @Getter
    private DefaultMutableTreeNode rootNode;

    public TestRunTabController(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.rootNode = new DefaultMutableTreeNode("loading..");
    }

    public void buildTreeAsync(TestProject selectedTestProject) {
        DefaultMutableTreeNode localRoot = new DefaultMutableTreeNode(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem().getTestRun());
        File testRunsFolder = selectedTestProject.getTestRun().getFile();

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (testRunsFolder.exists()) {
                File[] files = testRunsFolder.listFiles();
                if (files != null) {
                    Arrays.stream(files)
                            .map(DirectoryMapper::mapPackage)
                            .filter(Objects::nonNull)
                            .forEachOrdered(runDir -> localRoot.add(buildNodeRecursive(runDir)));
                }
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                this.rootNode = localRoot;
                if (projectPanel.getProjectTree() != null) {
                    projectPanel.getProjectTree().updateNodes();
                }
            });
        });
    }

    private DefaultMutableTreeNode buildNodeRecursive(@NotNull TestPackage dir) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        Optional.ofNullable(dir.getFile().listFiles())
                .stream()
                .flatMap(Arrays::stream)
                .map(DirectoryMapper::mapPackage)
                .filter(Objects::nonNull)
                .forEachOrdered(runDir -> node.add(buildNodeRecursive(runDir)));

        return node;
    }
}