package testGit.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.SimpleTree;
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

public class TestRunsDirectoryMapper {

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
        File[] testProjects = Config.getTestGitPath().toFile().listFiles();

        Optional.ofNullable(testProjects)
                .stream()
                .flatMap(Arrays::stream)
                .filter(item -> !item.getName().startsWith("."))
                .parallel()
                .map(TestRunsDirectoryMapper::map)
                .filter(Objects::nonNull)
                .forEach(item -> rootNode.add(buildNodeRecursive(item, "testRuns")));

        return rootNode;
    }

    public static DefaultMutableTreeNode buildNodeRecursive(Directory dir, String subFolder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        File folderToScan = (subFolder != null)
                ? dir.getFilePath().resolve(subFolder).toFile()
                : dir.getFile();

        Optional.ofNullable(folderToScan.listFiles())
                .stream()
                .flatMap(Arrays::stream)
                .parallel()
                .map(TestRunsDirectoryMapper::map)
                .filter(Objects::nonNull)
                .forEachOrdered(runDir -> node.add(buildNodeRecursive(runDir, null)));

        return node;
    }

    @Nullable
    public static Directory map(final File file) {
        try {
            String fileName = file.getName();
            String rawName = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf(".")) : fileName;

            String[] parts = rawName.split("_", 3);
            if (parts.length < 3)
                Notifier.error("Test run Error", "invalid name: " + rawName);

            return new Directory()
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName())
                    .setType(DirectoryType.valueOf(parts[0].toUpperCase()))
                    .setName(parts[1])
                    .setActive(Integer.parseInt(parts[2]));
        } catch (Exception e) {
            Notifier.error("mapping Failed", e.getMessage());
            return null;
        }
    }
}