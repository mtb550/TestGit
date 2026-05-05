package org.testin.util.automationGenerator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.testin.settings.AppSettingsState;
import org.testin.util.Tools;

import java.util.List;

public class CreateTestProject implements GeneratorAction {

    @Override
    public void execute(final @NotNull Project project, final @NotNull String targetName, final @NotNull List<String> fqcn) {
        if (targetName.isEmpty()) return;

        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    VirtualFile sourceRoot = Tools.getMainSourceRoot(project);

                    if (sourceRoot != null) {
                        String basePath = AppSettingsState.getInstance().rootAutomationPath;

                        String safePackageName = Tools.toCamelCase(targetName);

                        String relativePackagePath = (basePath != null && !basePath.trim().isEmpty())
                                ? basePath.replace(".", "/") + "/" + safePackageName
                                : safePackageName;

                        VirtualFile newPackage = VfsUtil.createDirectoryIfMissing(sourceRoot, relativePackagePath);

                        if (newPackage != null) {
                            System.out.println("[TRACE] Successfully created project package: " + newPackage.getPath());
                        }
                    } else {
                        System.out.println("[WARNING] No Source Root found in the project.");
                    }
                } catch (Exception ex) {
                    System.err.println("[ERROR] Failed to create project package: " + ex.getMessage());
                }
            });
        });
    }
}