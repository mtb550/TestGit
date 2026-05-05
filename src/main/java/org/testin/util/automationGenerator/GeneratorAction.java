package org.testin.util.automationGenerator;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface GeneratorAction {
    void execute(final @NotNull Project project, final @NotNull String targetName, final @NotNull List<String> fqcn);
}