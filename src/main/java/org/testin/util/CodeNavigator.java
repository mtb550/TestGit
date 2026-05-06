package org.testin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.pom.Navigatable;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.Config;

import java.util.ArrayList;
import java.util.List;

public class CodeNavigator {

    public void toCode(final @NotNull List<String> rawFqcn, final @NotNull String testCaseName) {
        if (rawFqcn.isEmpty()) {
            Messages.showWarningDialog("This test case has no automation reference.", "Missing Reference");
            return;
        }

        final Project project = Config.getProject();
        final String methodName = Tools.toCamelCase(testCaseName);

        ApplicationManager.getApplication().executeOnPooledThread(() ->
                ApplicationManager.getApplication().runReadAction(() -> {

                    List<String> cleanedFqcn = sanitizeFqcn(rawFqcn);

                    if (cleanedFqcn.isEmpty()) {
                        ApplicationManager.getApplication().invokeLater(() ->
                                Messages.showErrorDialog("Invalid FQCN path format.", "Navigation Error")
                        );
                        return;
                    }

                    List<String> packageList = new ArrayList<>(cleanedFqcn);
                    String baseClassName = packageList.removeLast();
                    String expectedClassName = Tools.toPascalCase(baseClassName);

                    if (expectedClassName.toLowerCase().endsWith("test")) {
                        if (expectedClassName.endsWith("test")) {
                            expectedClassName = expectedClassName.substring(0, expectedClassName.length() - 4) + "Test";
                        }
                    } else {
                        expectedClassName += "Test";
                    }

                    String fqcnString = String.join(".", packageList).toLowerCase() + "." + expectedClassName;
                    System.out.println("[NAVIGATOR] Searching for cleaned FQCN: " + fqcnString);

                    PsiClass targetClass = JavaPsiFacade.getInstance(project)
                            .findClass(fqcnString, GlobalSearchScope.projectScope(project));

                    if (targetClass != null) {
                        Navigatable targetElement = targetClass;
                        PsiMethod[] exactMethods = targetClass.findMethodsByName(methodName, false);

                        if (exactMethods.length > 0) {
                            targetElement = exactMethods[0];
                        } else {
                            for (PsiMethod method : targetClass.getMethods()) {
                                if (method.getName().equalsIgnoreCase(methodName)) {
                                    targetElement = method;
                                    break;
                                }
                            }
                        }

                        final Navigatable finalTarget = targetElement;
                        ApplicationManager.getApplication().invokeLater(() -> {
                            if (finalTarget.canNavigate()) {
                                finalTarget.navigate(true);
                            }
                        });

                    } else {
                        ApplicationManager.getApplication().invokeLater(() ->
                                Messages.showErrorDialog("Could not find class: " + fqcnString, "Class Not Found")
                        );
                    }
                })
        );
    }

    private List<String> sanitizeFqcn(List<String> rawFqcn) {
        List<String> sanitized = new ArrayList<>();
        boolean startAdding = false;
        for (String part : rawFqcn) {
            if (startAdding) {
                if (!part.equalsIgnoreCase("testCases")) {
                    sanitized.add(part.replace(" ", "").toLowerCase());
                }
            }
            if (part.equalsIgnoreCase("testin")) startAdding = true;
        }
        return sanitized;
    }
}