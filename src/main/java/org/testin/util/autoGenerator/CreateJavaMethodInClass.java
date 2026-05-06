package org.testin.util.autoGenerator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.testin.util.Tools;

import java.util.ArrayList;
import java.util.List;

public class CreateJavaMethodInClass {

    public void execute(@NotNull final Project project, @NotNull final List<String> rawFqcn, @NotNull final String testCaseName) {
        if (rawFqcn.isEmpty() || testCaseName.isEmpty()) return;

        ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(project, "Create Test Method", null, () -> {
            try {
                List<String> cleanedFqcn = sanitizeFqcn(rawFqcn);

                if (cleanedFqcn.isEmpty()) {
                    System.err.println("Failed to sanitize FQCN. Path might be outside testin directory.");
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

                String packageName = String.join(".", packageList).toLowerCase();
                String fqcnString = packageName + "." + expectedClassName;
                String methodName = Tools.toCamelCase(testCaseName);

                System.out.println("[DEBUG] Corrected FQCN: " + fqcnString);

                JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
                GlobalSearchScope scope = GlobalSearchScope.projectScope(project);

                PsiClass targetClass = psiFacade.findClass(fqcnString, scope);

                if (targetClass == null) {
                    VirtualFile sourceRoot = Tools.getMainSourceRoot(project);
                    if (sourceRoot != null) {
                        String relativePath = String.join("/", packageList);
                        VirtualFile packageDir = VfsUtil.createDirectoryIfMissing(sourceRoot, relativePath);

                        if (packageDir != null) {
                            String fileName = expectedClassName + ".java";
                            VirtualFile javaFile = packageDir.findChild(fileName);

                            if (javaFile == null) {
                                javaFile = packageDir.createChildData(this, fileName);
                                String basePackage = String.join(".", packageList);
                                String fileContent = "package " + basePackage + ";\n\n" +
                                        "public class " + expectedClassName + " {\n" +
                                        "    \n" +
                                        "}\n";
                                VfsUtil.saveText(javaFile, fileContent);
                            }
                        }
                    }

                    PsiDocumentManager.getInstance(project).commitAllDocuments();
                    targetClass = psiFacade.findClass(fqcnString, scope);
                }

                if (targetClass != null) {
                    injectMethod(project, targetClass, methodName, testCaseName);
                } else {
                    System.err.println("Critical Error: Class still not found for FQCN: " + fqcnString);
                }

            } catch (Exception ex) {
                System.err.println("Failed to inject Java method: " + ex.getMessage());
            }
        }));
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

            if (part.equalsIgnoreCase("testin")) {
                startAdding = true;
            }
        }
        return sanitized;
    }

    private void injectMethod(Project project, PsiClass targetClass, String methodName, String testCaseName) {
        PsiMethod[] existingMethods = targetClass.findMethodsByName(methodName, false);
        if (existingMethods.length == 0) {
            PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

            PsiFile file = targetClass.getContainingFile();
            if (file instanceof PsiJavaFile javaFile) {
                PsiImportList importList = javaFile.getImportList();
                if (importList != null && importList.findSingleClassImportStatement("org.testng.annotations.Test") == null) {
                    PsiClass testClass = JavaPsiFacade.getInstance(project).findClass("org.testng.annotations.Test", GlobalSearchScope.allScope(project));
                    if (testClass != null) importList.add(factory.createImportStatement(testClass));
                }
            }

            String methodText = "@Test\n" +
                    "public void " + methodName + "() {\n" +
                    "    // TODO: Auto-generated test steps for " + testCaseName + "\n" +
                    "}";

            PsiMethod newMethod = factory.createMethodFromText(methodText, targetClass);
            PsiElement addedElement = targetClass.add(newMethod);
            CodeStyleManager.getInstance(project).reformat(addedElement);
            System.out.println("Method " + methodName + " injected successfully.");
        }
    }
}