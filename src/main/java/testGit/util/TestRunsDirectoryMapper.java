package testGit.util;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Config;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;
import testGit.projectPanel.projectSelector.ProjectSelector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class TestRunsDirectoryMapper {
    // Aligned field name: testRunsTreeModel
    @Getter
    @Setter
    private static DefaultTreeModel treeModel;

    /**
     * Builds the Test Runs tree.
     * Matches the 'buildTree' naming in TestCasesDirectoryMapper.
     */
    public static void buildTree() {
        treeModel = new DefaultTreeModel(buildRoot(ProjectSelector.getSelectedProject().getName(), "testRuns"));
    }

    private static DefaultMutableTreeNode buildRoot(String rootName, String subFolderName) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootName);
        File[] projects = Config.getRootFolderFile().listFiles(File::isDirectory);

        if (projects != null) {
            Arrays.stream(projects)
                    .filter(file -> !file.getName().startsWith("."))
                    .map(TestRunsDirectoryMapper::map)
                    .filter(Objects::nonNull)
                    .forEach(dir -> rootNode.add(buildNodeRecursive(dir, subFolderName)));
        }
        return rootNode;
    }

    /**
     * Recursive-style node builder to match TestCasesDirectoryMapper structure.
     */
    public static DefaultMutableTreeNode buildNodeRecursive(@NotNull Directory dir, @Nullable String subFolder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        File folderToScan = (subFolder != null && dir.getFilePath() != null)
                ? dir.getFilePath().resolve(subFolder).toFile()
                : dir.getFile();

        if (folderToScan != null && folderToScan.exists() && folderToScan.isDirectory()) {
            // Scan for .json files (Test Run files)
            File[] runFiles = folderToScan.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".json"));

            if (runFiles != null) {
                Arrays.stream(runFiles)
                        .map(TestRunsDirectoryMapper::map) // Using unified 'map' name
                        .filter(Objects::nonNull)
                        .forEach(runDir -> node.add(new DefaultMutableTreeNode(runDir)));
            }
        }
        return node;
    }

    /**
     * Maps a File to a Directory object.
     * Renamed from 'mapToRun' to 'map' to match TestCasesDirectoryMapper.
     */
    @Nullable
    public static Directory map(@NotNull final File file) {
        try {
            String fileName = file.getName();
            // Handle extension for files, keep as is for directories
            String rawName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;

            String[] parts = rawName.split("_", 3);
            if (parts.length < 3) return null;

            return new Directory()
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName())
                    .setType(DirectoryType.TR)
                    .setName(parts[1])
                    .setActive(Integer.parseInt(parts[2]));
        } catch (Exception e) {
            return null;
        }
    }
}