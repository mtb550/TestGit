package org.testin.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.Config;
import org.testin.util.runner.TestCaseExecutionTracker;

import java.nio.file.Path;
import java.util.Optional;

public class StartupActivity {
    public static void execute(@NotNull Project project) {
        System.out.println("StartupActivity.execute()");

        AppSettingsState settings = AppSettingsState.getInstance();

        Path testinPath;
        if (settings.rootTestinPath != null && !settings.rootTestinPath.isEmpty()) {
            testinPath = Path.of(settings.rootTestinPath);
        } else {
            testinPath = Optional.ofNullable(project.getBasePath())
                    .map(Path::of)
                    .map(p -> p.resolve("org/testin"))
                    .orElse(null);
        }

        Path automationPath = null;
        if (settings.rootAutomationPath != null && !settings.rootAutomationPath.isEmpty()) {
            String folderFormat = settings.rootAutomationPath.replace(".", "/");

            automationPath = Optional.ofNullable(project.getBasePath())
                    .map(Path::of)
                    .map(p -> p.resolve(folderFormat))
                    .orElse(null);
        }

        System.out.println("testin Path: " + testinPath);
        System.out.println("automation Path: " + automationPath);

        Config.setTestinPath(testinPath);
        Config.setAutomationPath(automationPath);

        /// to be removed
        Config.setProject(project);

        TestCaseExecutionTracker.initGlobalListener(Config.getProject());
    }
}