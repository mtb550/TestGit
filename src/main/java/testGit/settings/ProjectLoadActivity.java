package testGit.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Config;

import java.nio.file.Path;
import java.util.Optional;

public class ProjectLoadActivity implements ProjectActivity {
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        System.out.println("ProjectLoadActivity.execute()");

        Path testGitPath = Optional.ofNullable(project.getBasePath())
                .map(Path::of)
                .map(p -> p.resolve("testGit"))
                .orElse(null);

        System.out.println("testGit Path" + testGitPath);

        Config.setTestGitPath(testGitPath);
        Config.setProject(project);

        return Unit.INSTANCE;
    }
}