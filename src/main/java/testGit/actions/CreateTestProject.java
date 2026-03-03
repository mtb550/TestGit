package testGit.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Config;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;
import testGit.projectPanel.ProjectPanel;
import testGit.ui.AddNewTestProjectDialog;
import testGit.util.Notifier;

import java.io.IOException;
import java.nio.file.Path;

public class CreateTestProject extends DumbAwareAction {
    public final ProjectPanel projectPanel;

    public CreateTestProject(ProjectPanel projectPanel) {
        super("New Test Project", "Create a new test project", AllIcons.General.Add);
        this.projectPanel = projectPanel;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String name = AddNewTestProjectDialog.show();

        if (name == null) return;

        Directory newTestProject = new Directory()
                .setType(DirectoryType.PR)
                .setName(name)
                .setActive(1);

        String folderName = String.format("%s_%s_%d", newTestProject.getType().name().toLowerCase(), newTestProject.getName(), newTestProject.getActive());
        Path projectPath = Config.getTestGitPath().resolve(folderName);

        newTestProject.setFileName(folderName)
                .setFilePath(projectPath)
                .setFile(projectPath.toFile());

        WriteAction.run(() -> {
            try {
                VirtualFile rootVf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Config.getTestGitPath().toFile());

                if (rootVf != null) {
                    VirtualFile projectDir = rootVf.createChildDirectory(this, folderName);

                    projectDir.createChildDirectory(this, "testCases");
                    projectDir.createChildDirectory(this, "testRuns");

                    projectPanel.getTestProjectSelector().addTestProject(newTestProject);
                    projectPanel.getTestProjectSelector().getSelectedTestProject().setSelectedItem(newTestProject);
                    projectPanel.getTestProjectSelector().filterByTestProject(newTestProject, projectPanel);

                    Notifier.information("New Test Project", String.format("Test Project %s has been added", name));

                }
            } catch (IOException ex) {
                Messages.showErrorDialog("Error creating project structure: " + ex.getMessage(), "IO Error");
            }
        });
    }
}