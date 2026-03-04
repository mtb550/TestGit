package testGit.projectPanel.testRunTab;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;
import testGit.projectPanel.ProjectPanel;
import testGit.util.Notifier;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TestRunTabController {
    private final ProjectPanel projectPanel;
    @Getter
    private final SimpleTree tree;

    public TestRunTabController(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.tree = new SimpleTree();
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

    public void init() {
        System.out.println("TestRunTabController.init()");

        tree.setCellRenderer(new TestRunRenderer());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(false);
        tree.addMouseListener(new MouseAdapterImpl(projectPanel));
        ShortcutHandler.register(projectPanel, tree);

        buildTreeAsync(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem());
    }

    public void buildTreeAsync(Directory selectedProject) {
        System.out.println("TestRunTabController.buildTreeAsync()");
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            DefaultMutableTreeNode root = buildNodeRecursive(selectedProject, "testRuns");
            DefaultTreeModel newModel = new DefaultTreeModel(root);

            ApplicationManager.getApplication().invokeLater(() -> {
                this.tree.setModel(newModel);
                this.tree.setRootVisible(true);
                this.tree.revalidate();
                this.tree.repaint();
            });
        });
    }

    private DefaultMutableTreeNode buildNodeRecursive(Directory dir, String subFolder) {
        System.out.println("TR buildNodeRecursive");
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        File folderToScan = (subFolder != null)
                ? dir.getFilePath().resolve(subFolder).toFile()
                : dir.getFile();

        Optional.ofNullable(folderToScan.listFiles())
                .stream()
                .flatMap(Arrays::stream)
                //.parallel()
                .map(TestRunTabController::map)
                .filter(Objects::nonNull)
                .forEachOrdered(runDir -> node.add(buildNodeRecursive(runDir, null)));

        return node;
    }
}