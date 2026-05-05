package org.testin.util.automationGenerator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UpdateTestCase implements GeneratorAction {

    public void execute(final @NotNull Project project, final @NotNull String targetName, final @NotNull List<String> fqcn) {
        if (fqcn.isEmpty() || targetName.isEmpty()) return;

        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, "Create Test Method", null, () -> {
                try {

                } catch (Exception ex) {
                    System.err.println("[ERROR] Failed to inject Java method: " + ex.getMessage());
                }
            });
        });
    }

}
