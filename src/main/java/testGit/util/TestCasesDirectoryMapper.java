package testGit.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Config;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TestCasesDirectoryMapper {

    public static void buildTreeAsync(Directory selectedProject, SimpleTree tree) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            // to be removed after fix
            String rootName = (selectedProject != null) ? selectedProject.getName() : "All Projects";

            DefaultMutableTreeNode root = buildRoot(rootName);
            DefaultTreeModel newModel = new DefaultTreeModel(root);
            tree.setModel(newModel);
        });
    }

    private static DefaultMutableTreeNode buildRoot(String rootName) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootName);
        File[] testProjects = Config.getTestGitPath().toFile().listFiles(File::isDirectory);

        Optional.ofNullable(testProjects)
                .stream()
                .flatMap(Arrays::stream)
                .filter(item -> !item.getName().startsWith("."))
                .parallel()
                .map(TestCasesDirectoryMapper::map)
                .filter(Objects::nonNull)
                .forEach(item -> rootNode.add(buildNodeRecursive(item, "testCases")));

        return rootNode;
    }

    public static DefaultMutableTreeNode buildNodeRecursive(@NotNull Directory dir, @Nullable String subFolder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        File folderToScan = (subFolder != null)
                ? dir.getFilePath().resolve(subFolder).toFile()
                : dir.getFile();

        Optional.ofNullable(folderToScan.listFiles(File::isDirectory)) // Handles the null check safely
                .stream()
                .flatMap(Arrays::stream)
                .parallel()
                .map(TestCasesDirectoryMapper::map)
                .filter(Objects::nonNull)
                .forEachOrdered(caseDir -> node.add(buildNodeRecursive(caseDir, null)));

        return node;
    }

    @Nullable
    public static Directory map(@NotNull final File file) {
        try {
            String[] parts = file.getName().split("_", 3);

            return new Directory()
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName())
                    .setType(DirectoryType.valueOf(parts[0].toUpperCase()))
                    .setName(parts[1])
                    .setActive(Integer.parseInt(parts[2]));
        } catch (Exception e) {
            Notifier.error("Read Test Case Failed", "Skipping invalid directory format: " + file.getName());
            return null;
        }
    }
}