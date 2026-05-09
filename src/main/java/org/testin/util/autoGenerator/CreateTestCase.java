package org.testin.util.autoGenerator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testin.util.Tools;

import javax.swing.tree.TreePath;
import java.util.List;

public class CreateTestCase implements GeneratorAction {

    public void execute(final @NotNull Project project, final @NotNull String targetName, final @Nullable TreePath path) {
        if (path == null) return;
        List<String> fqcn = Tools.getInstance().extractFqcn(path);
        if (fqcn.isEmpty() || targetName.isEmpty()) return;

        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, "Create Test Method", null, () -> {
                try {
                    String fqcnString = String.join(".", fqcn);

                    if (!fqcnString.endsWith("Test")) {
                        fqcnString += "Test";
                    }

                    String methodName = Tools.getInstance().toCamelCase(targetName);

                    PsiClass targetClass = JavaPsiFacade.getInstance(project)
                            .findClass(fqcnString, GlobalSearchScope.projectScope(project));

                    if (targetClass != null) {
                        PsiMethod[] existingMethods = targetClass.findMethodsByName(methodName, false);

                        if (existingMethods.length == 0) {
                            PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

                            String methodText = "@Test\n" +
                                    "public void " + methodName + "() {\n" +
                                    "    // TODO: Auto-generated test steps for " + targetName + "\n" +
                                    "}";

                            PsiMethod newMethod = factory.createMethodFromText(methodText, targetClass);
                            PsiElement addedElement = targetClass.add(newMethod);
                            CodeStyleManager.getInstance(project).reformat(addedElement);

                            System.out.println("[TRACE] Successfully injected method: " + methodName + " into " + fqcnString);
                        } else {
                            System.out.println("[WARNING] Method already exists: " + methodName);
                        }
                    } else {
                        System.out.println("[WARNING] Could not find class for FQCN: " + fqcnString);
                    }
                } catch (Exception ex) {
                    System.err.println("[ERROR] Failed to inject Java method: " + ex.getMessage());
                }
            });
        });
    }

}
