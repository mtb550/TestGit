package testGit.util;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.ui.treeStructure.SimpleTree;
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

public class TestCasesDirectoryMapper {
    @Getter
    @Setter
    private static DefaultTreeModel treeModel;

    /**
     * The background-friendly way to load your tree.
     */
    public static void buildTreeAsync(@NotNull SimpleTree tree) {
        ProgressManager.getInstance().run(new Task.Backgroundable(Config.getProject(), "Loading test cases", false) {
            private DefaultTreeModel newModel;

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                // This runs on a BACKGROUND thread
                indicator.setIndeterminate(true);
                indicator.setText("Scanning directories for test cases...");

                // Do the heavy IO work here
                String rootName = ProjectSelector.getSelectedProject().getName();
                DefaultMutableTreeNode root = buildRoot(rootName, "testCases");
                newModel = new DefaultTreeModel(root);
            }

            @Override
            public void onSuccess() {
                // This runs on the UI thread (EDT)
                treeModel = newModel;
                tree.setModel(treeModel);

                // Optional: Expand the first level
                tree.expandRow(0);
                System.out.println("Tree loaded successfully in background.");
            }
        });
    }

    /**
     * دالة عامة لبناء الجذر الأساسي لتجنب تكرار الكود
     */
    private static DefaultMutableTreeNode buildRoot(String rootName, String subFolderName) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootName);
        File[] projects = Config.getRootFolderFile().listFiles(File::isDirectory);

        if (projects != null) {
            Arrays.stream(projects)
                    .filter(file -> !file.getName().startsWith("."))
                    .map(TestCasesDirectoryMapper::map)
                    .filter(Objects::nonNull)
                    .forEach(dir -> {
                        System.out.println("TestCasesDirectoryMapper.buildRoot(). " + dir.getName());
                        rootNode.add(buildNodeRecursive(dir, subFolderName));
                    });
        }
        return rootNode;
    }

    /**
     * دالة التكرار الذاتي (Recursion) الموحدة لكل أنواع الأشجار
     */
    public static DefaultMutableTreeNode buildNodeRecursive(@NotNull Directory dir, @Nullable String subFolder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        File folderToScan = (subFolder != null && dir.getFilePath() != null)
                ? dir.getFilePath().resolve(subFolder).toFile()
                : dir.getFile();

        File[] children = folderToScan.listFiles(File::isDirectory);

        if (children != null) {
            for (File childFile : children) {
                Directory childDir = map(childFile);
                if (childDir != null) {
                    node.add(buildNodeRecursive(childDir, null));
                }
            }
        }
        return node;
    }

    /**
     * تحويل File إلى كائن Directory مع معالجة الأخطاء
     */
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
            System.err.println("Skipping invalid directory format: " + file.getName());
            return null;
        }
    }
}