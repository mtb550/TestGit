package org.testin.util.autoGenerator;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;

@FunctionalInterface
public interface GeneratorAction {
    void execute(final @NotNull Project project, final @NotNull String targetName, final @Nullable TreePath fqcn);
}