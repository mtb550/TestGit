package org.testin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.testin.pojo.Config;
import org.testin.pojo.DirectoryType;
import org.testin.pojo.Group;
import org.testin.pojo.Priority;
import org.testin.pojo.dto.dirs.DirectoryDto;
import org.testin.pojo.dto.dirs.TestProjectDirectoryDto;
import org.testin.settings.AppSettingsState;
import org.testin.util.notifications.Notifier;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Tools {

    private static final Tools INSTANCE = new Tools();

    private final Pattern SANITIZE_PATTERN = Pattern.compile("[^a-zA-Z0-9 _]");
    private final Pattern STEP_MUSH_PATTERN = Pattern.compile(".*\\s\\d+[-.].*");
    private final Pattern STEP_LINE_PATTERN = Pattern.compile("(\\s)(?=\\d+[-.])");
    private final Pattern STEP_CLEAN_PATTERN = Pattern.compile("^\\d+[-.]\\s*");


    public static Tools getInstance() {
        return INSTANCE;
    }

    public String sanitizePackageName(final @NotNull String name) {
        String removeKeyword = name.replace("-test-cases", "");
        String cleanName = SANITIZE_PATTERN.matcher(removeKeyword).replaceAll("").trim();
        String[] split = cleanName.split("[\\s_]+");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String word = split[i];
            if (word.isEmpty()) continue;
            if (i == 0) sb.append(word.toLowerCase());
            else sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
        }

        String result = sb.toString();

        if (result.isEmpty()) return "generated" + System.currentTimeMillis();

        if (Character.isDigit(result.charAt(0))) result = "_" + result;
        return result;
    }

    public String sanitizeClassName(final @NotNull String name) {
        if (name.trim().isEmpty()) {
            return "DefaultTest";
        }

        String cleanName = SANITIZE_PATTERN.matcher(name).replaceAll("").trim();
        String[] split = cleanName.split("[\\s_]+");

        StringBuilder sb = new StringBuilder();
        for (String word : split) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }

        String result = sb.toString();

        if (result.isEmpty()) return "DefaultTest";

        if (Character.isDigit(result.charAt(0))) result = "_" + result;

        if (!result.toLowerCase().endsWith("test")) result += "Test";
        else if (result.equalsIgnoreCase("test")) result = "Test";

        return result;
    }

    public List<String> appendFqcn(final List<String> pFqcn, final String s, final DirectoryType t) {
        List<String> newFqcn = new ArrayList<>(pFqcn.size() + 1);
        newFqcn.addAll(pFqcn);

        if (t == DirectoryType.TSP || t == DirectoryType.TP)
            newFqcn.add(sanitizePackageName(s));

        else if (t == DirectoryType.TS)
            newFqcn.add(sanitizeClassName(s));

        return newFqcn;
    }

    public Path getProjectPath(final SimpleTree tree) {
        final DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        if (root != null && root.getUserObject() instanceof TestProjectDirectoryDto dir)
            return dir.getPath();
        return null;
    }

    public DirectoryDto getCurrentSelectedDirectory(final SimpleTree tree) {
        TreePath path = tree.getSelectionPath();
        if (path == null) return null;

        final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) path.getLastPathComponent();

        if (parentNode.getUserObject() instanceof DirectoryDto parentDir) {
            return parentDir;
        }

        return null;
    }

    @NotNull
    public String format(@Nullable final String text) {
        if (StringUtil.isEmptyOrSpaces(text)) return "";
        String s = text.trim();
        return StringUtil.capitalize(s) + ".";
    }

    public String toCamelCase(final String text) {
        if (text == null || text.isEmpty()) return text;
        String[] words = text.split("[\\W_]+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.isEmpty()) continue;
            if (i == 0) {
                result.append(word.substring(0, 1).toLowerCase()).append(word.substring(1).toLowerCase());
            } else {
                result.append(word.substring(0, 1).toUpperCase()).append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }

    public @NotNull List<String> extractLogicalPath(final @NotNull Path path) {
        if (path.toString().isEmpty()) return new ArrayList<>();

        String pathStr = path.toAbsolutePath().toString().replace("\\", "/");

        int markerIndex = pathStr.indexOf("/org/testin/");
        if (markerIndex != -1) {
            pathStr = pathStr.substring(markerIndex + "/org/testin/".length());
        } else if (pathStr.startsWith("org/testin/")) {
            pathStr = pathStr.substring("org/testin/".length());
        }

        String[] segments = pathStr.split("/");
        List<String> logicalPath = new ArrayList<>();

        for (String segment : segments) {
            if (segment.isEmpty() || segment.equals(DirectoryType.TCD.getPathName())) continue;

            logicalPath.add(segment);
        }

        return logicalPath;
    }

    public void updateChildrenPathsRecursive(final DefaultMutableTreeNode parentNode, final Path oldParentPath, final Path newParentPath) {
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);

            if (childNode.getUserObject() instanceof DirectoryDto childDir) {

                Path relativePath = oldParentPath.relativize(childDir.getPath());
                Path newChildPath = newParentPath.resolve(relativePath);

                childDir.setPath(newChildPath);
                updateChildrenPathsRecursive(childNode, oldParentPath, newParentPath);
            }
        }
    }

    public String getFormattedDuration(final Duration duration) {
        if (duration == null) return null;
        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }

    public void createJavaClassInTestRoot(@NotNull final Project project, @NotNull final String packageName, @NotNull final String className) {

        ApplicationManager.getApplication().invokeLater(() ->
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        ProjectRootManager rootManager = ProjectRootManager.getInstance(project);

                        VirtualFile testRoot = Arrays.stream(rootManager.getContentSourceRoots())
                                .filter(root -> rootManager.getFileIndex().isInTestSourceContent(root))
                                .findFirst()
                                .orElse(null);

                        if (testRoot != null) {
                            String basePath = AppSettingsState.getInstance().rootAutomationPath;

                            String safePackageName = !packageName.isEmpty() ? toCamelCase(packageName) : "";

                            String safeCamelClass = toCamelCase(className);
                            String safeClassName = safeCamelClass.substring(0, 1).toUpperCase() + safeCamelClass.substring(1);
                            safeClassName += "Test";

                            String relativePackagePath = basePath != null && !basePath.trim().isEmpty() ? basePath.replace(".", "/") : "";
                            String fullPackageDeclaration = basePath != null && !basePath.trim().isEmpty() ? basePath : "";

                            if (!safePackageName.isEmpty()) {
                                relativePackagePath = relativePackagePath.isEmpty() ? safePackageName : relativePackagePath + "/" + safePackageName;
                                fullPackageDeclaration = fullPackageDeclaration.isEmpty() ? safePackageName : fullPackageDeclaration + "." + safePackageName;
                            }

                            VirtualFile targetDirectory = VfsUtil.createDirectoryIfMissing(testRoot, relativePackagePath);

                            if (targetDirectory != null) {
                                String fileName = safeClassName + ".java";
                                VirtualFile existingFile = targetDirectory.findChild(fileName);

                                if (existingFile == null) {
                                    VirtualFile newClassFile = targetDirectory.createChildData(Tools.class, fileName);

                                    String classContent = buildClassContent(fullPackageDeclaration, safeClassName);
                                    VfsUtil.saveText(newClassFile, classContent);

                                    System.out.println("[TRACE] Successfully created Java class: " + newClassFile.getPath());

                                } else {
                                    System.out.println("[WARNING] Java class already exists: " + fileName);
                                }
                            }
                        } else {
                            System.out.println("[WARNING] No Test Source Root found in the project.");
                        }
                    } catch (Exception ex) {
                        System.err.println("[ERROR] Failed to create Java class: " + ex.getMessage());
                    }
                }));
    }

    private String buildClassContent(String fullPackageName, String className) {
        StringBuilder content = new StringBuilder();

        if (fullPackageName != null && !fullPackageName.isEmpty()) {
            content.append("package ").append(fullPackageName).append(";\n\n");
        }

        content.append("public class ").append(className).append(" {\n\n");
        content.append("    // TODO: Auto-generated test class\n\n");
        content.append("}\n");

        return content.toString();
    }

    public @Nullable VirtualFile getTestSourceRoot(final @NotNull Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();

        for (Module module : modules) {
            List<VirtualFile> sourceRoots = ModuleRootManager.getInstance(module)
                    .getSourceRoots(JavaSourceRootType.TEST_SOURCE);

            if (!sourceRoots.isEmpty()) {
                System.out.println("[TRACE] Found test source root: " + sourceRoots.getFirst());
                return sourceRoots.getFirst();
            }
        }

        System.out.println("[WARNING] No Test Source Root found in the project.");
        return null;
    }

    public String toPascalCase(String text) {
        if (text == null || text.trim().isEmpty()) return "";

        String[] words = text.split("[\\s_\\-]+");
        StringBuilder pascalCase = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                pascalCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }
        return pascalCase.toString();
    }

    public void openWithAssociatedProgram(VirtualFile virtualFile) {
        if (virtualFile == null || !virtualFile.exists()) {
            Notifier.getInstance().error("Open Error", "The file does not exist.");
            return;
        }

        File file = new File(virtualFile.getPath());

        if (!Desktop.isDesktopSupported()) {
            Notifier.getInstance().error("System Error", "Desktop operations are not supported on this system.");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            Notifier.getInstance().error("System Error", "The 'Open' action is not supported on this system.");
            return;
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                desktop.open(file);
            } catch (IOException e) {
                ApplicationManager.getApplication().invokeLater(() ->
                        Notifier.getInstance().error("Execution Error", "Failed to open the file: " + e.getMessage())
                );
            }
        });
    }

    public String sanitizeDescription(final String rawDesc) {
        if (rawDesc == null || rawDesc.isBlank()) return "EMPTY_DESCRIPTION";
        String cleaned = SANITIZE_PATTERN.matcher(rawDesc).replaceAll("").trim();
        return cleaned.isEmpty() ? "EMPTY_DESCRIPTION" : cleaned;
    }

    public List<String> parseStepsSafe(final String stepsRaw) {
        if (stepsRaw == null || stepsRaw.isBlank()) {
            return new ArrayList<>();
        }

        String text = stepsRaw;

        if (!text.contains("\n") && STEP_MUSH_PATTERN.matcher(text).matches()) {
            text = STEP_LINE_PATTERN.matcher(text).replaceAll("\n");
        }

        return Arrays.stream(text.split("\n"))
                .map(line -> STEP_CLEAN_PATTERN.matcher(line).replaceFirst("").trim())
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    public Priority parsePrioritySafe(final String priorityStr) {
        if (priorityStr == null || priorityStr.isBlank()) {
            return Priority.LOW;
        }
        try {
            return Priority.valueOf(priorityStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Priority.LOW;
        }
    }

    public ZonedDateTime parseDateSafe(final String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
        try {
            return LocalDateTime.parse(dateStr, Config.EXCEL_DATE_FORMATTER).atZone(ZoneId.systemDefault());
        } catch (Exception e) {
            return ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        }
    }

    public List<Group> parseGroupsSafe(final String rawGroups) {
        if (rawGroups == null || rawGroups.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(rawGroups.split(","))
                .map(String::trim)
                .filter(g -> !g.isEmpty())
                .map(String::toUpperCase)
                .map(groupName -> {
                    try {
                        return Group.valueOf(groupName);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<String> sanitizeFqcn(final List<String> rawFqcn) {
        List<String> sanitized = new ArrayList<>();
        for (String part : rawFqcn) {
            if (part.contains("/") || part.contains("\\")) {
                continue;
            }
            if (!part.equalsIgnoreCase(DirectoryType.TCD.getPathName())) {
                sanitized.add(part.replace(" ", ""));
            }
        }
        return sanitized;
    }

    public String sanitizeMethodName(final String description) {
        if (description == null || description.isEmpty()) return "testMethod";

        String[] words = description.split("[^a-zA-Z0-9]+");
        StringBuilder methodName = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) continue;

            if (methodName.isEmpty()) {
                methodName.append(word.toLowerCase());
            } else {
                methodName.append(word.substring(0, 1).toUpperCase());
                if (word.length() > 1) {
                    methodName.append(word.substring(1).toLowerCase());
                }
            }
        }
        return methodName.toString();
    }

}