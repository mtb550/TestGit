package testGit.pojo;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import testGit.settings.AppSettingsState;

import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration manager for the TestGit plugin.
 * Handles path resolution and shared UI constants.
 */
public class Config {

    // --- Icons ---
    public static final Icon PROJECT_ICON = AllIcons.Nodes.Project;
    public static final Icon FOLDER_ICON  = AllIcons.Nodes.Folder;
    public static final Icon CLASS_ICON   = AllIcons.Nodes.Class;
    public static final Icon JAVA_ICON    = AllIcons.FileTypes.Java;
    public static final Icon JSON_ICON    = AllIcons.FileTypes.Json;
    public static final Icon XML_ICON     = AllIcons.FileTypes.Xml;
    public static final Icon TEXT_ICON    = AllIcons.FileTypes.Text;
    public static final Icon BRANCH_ICON  = AllIcons.Vcs.Branch;
    public static final Icon ADD_ICON     = AllIcons.General.Add;
    public static final Icon REMOVE_ICON  = AllIcons.General.Remove;

    @Getter @Setter private static Project project;
    @Getter @Setter private static String projectBasePath;

    private static String rootFolderPath;
    private static File rootFolderFile;

    /**
     * Retrieves the root directory for the plugin.
     * Defaults to {projectPath}/TestGit if not configured in settings.
     */
    public static File getRootFolderFile() {
        if (rootFolderFile == null) {
            initializePaths();
        }
        return rootFolderFile;
    }

    public static String getRootFolderPath() {
        if (rootFolderPath == null) {
            initializePaths();
        }
        return rootFolderPath;
    }

    /**
     * Resolves the directory for a specific project directory.
     * Since "testCases" and "testPlans" folders are removed, this returns the project directory.
     */
    public static File getProjectFolder(@NotNull Directory selectedProject) {
        Path path = Paths.get(getRootFolderPath(), selectedProject.getFileName());
        File folder = path.toFile();

        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Created project directory at: " + folder.getAbsolutePath());
            }
        }
        return folder;
    }

    public static synchronized void initializePaths() {
        if (rootFolderFile != null) return; // Double-check locking pattern

        String pathFromSettings = AppSettingsState.getInstance().rootFolderPath;

        if (pathFromSettings == null || pathFromSettings.trim().isEmpty()) {
            // Default to projectBasePath/TestGit
            if (projectBasePath == null) {
                throw new IllegalStateException("Project base path must be set before accessing Config.");
            }
            rootFolderPath = Paths.get(projectBasePath, "TestGit").toString();
        } else {
            rootFolderPath = pathFromSettings;
        }

        rootFolderFile = new File(rootFolderPath);

        System.out.println("[TestGit] Root Folder initialized at: " + rootFolderFile.getAbsolutePath());
    }
}