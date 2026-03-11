package testGit.settings;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Config;

import java.nio.file.Path;
import java.util.Optional;

public class StartupActivity {
    public static void execute(@NotNull Project project) {
        System.out.println("StartupActivity.execute()");

        // 1. Get the saved user settings
        AppSettingsState settings = AppSettingsState.getInstance();

        // 2. Resolve TestGit Path (Use settings if set, otherwise fallback to default)
        Path testGitPath;
        if (settings.rootTestGitPath != null && !settings.rootTestGitPath.isEmpty()) {
            testGitPath = Path.of(settings.rootTestGitPath);
        } else {
            testGitPath = Optional.ofNullable(project.getBasePath())
                    .map(Path::of)
                    .map(p -> p.resolve("testGit"))
                    .orElse(null);
        }

        // 3. Resolve Automation Path
        Path automationPath = null;
        if (settings.rootAutomationPath != null && !settings.rootAutomationPath.isEmpty()) {
            // Converts "src.test" into "src/test" so it forms a valid file system Path
            String folderFormat = settings.rootAutomationPath.replace(".", "/");

            automationPath = Optional.ofNullable(project.getBasePath())
                    .map(Path::of)
                    .map(p -> p.resolve(folderFormat))
                    .orElse(null);
        }

        System.out.println("testGit Path: " + testGitPath);
        System.out.println("automation Path: " + automationPath);

        // 4. Inject everything into the global Config POJO
        Config.setTestGitPath(testGitPath);
        Config.setAutomationPath(automationPath);
        Config.setProject(project);
    }
}