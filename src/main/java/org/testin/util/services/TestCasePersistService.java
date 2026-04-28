package org.testin.util.services;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;
import org.testin.pojo.Config;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.util.notifications.Notifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Service(Level.PROJECT)
public final class TestCasePersistService implements Disposable {
    public static TestCasePersistService getInstance(final Project project) {
        return project.getService(TestCasePersistService.class);
    }

    /**
     * @param path test set path
     * @param tcs  list of changed or created test cases
     */
    public void persist(final Path path, final @Nullable List<TestCaseDto> tcs) {
        if (path == null || tcs == null || tcs.isEmpty()) return;

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            tcs.stream()
                    .filter(Objects::nonNull)
                    .forEach(tc -> {
                        try {
                            File jsonFile = path.resolve(tc.getId() + ".json").toFile();
                            Config.getMapper().writeValue(jsonFile, tc);
                        } catch (IOException e) {
                            Notifier.error("Save Error", "Failed to persist data: " + e.getMessage());
                        }
                    });
            Notifier.info("Test Case Created", tcs.getFirst().getDescription());
        });
    }

    @Override
    public void dispose() {
        /// to be implemented
    }
}