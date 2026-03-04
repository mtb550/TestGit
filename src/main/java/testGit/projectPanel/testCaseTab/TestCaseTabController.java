package testGit.projectPanel.testCaseTab;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.Directory;
import testGit.pojo.DirectoryType;
import testGit.projectPanel.ProjectPanel;
import testGit.projectPanel.TransferHandlerImpl;
import testGit.util.Notifier;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.*;


public class TestCaseTabController {
    @Getter
    public final SimpleTree tree;
    private final ProjectPanel projectPanel;

    public TestCaseTabController(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.tree = new SimpleTree();
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

    public void init() {
        System.out.println("TestCaseTabController.init()");

        tree.setRootVisible(false);
        tree.setShowsRootHandles(false);
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);

        Set<DefaultMutableTreeNode> sharedCutNodes = new HashSet<>();
        tree.setCellRenderer(new TestCaseRenderer(sharedCutNodes));

        TransferHandlerImpl transferHandler = new TransferHandlerImpl(tree, sharedCutNodes);
        tree.setTransferHandler(transferHandler);
        ShortcutHandler.register(projectPanel, tree, transferHandler);
        tree.addMouseListener(new MouseAdapterImpl(projectPanel));

        System.out.println("once init tc: " + projectPanel.getTestProjectSelector().getSelectedTestProject().getItem());
        buildTreeAsync(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem());
    }

    public void buildTreeAsync(Directory selectedProject) {
        System.out.println("TestCaseTabController.buildTreeAsync()");
        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            DefaultMutableTreeNode root = buildNodeRecursive(selectedProject, "testCases");
            DefaultTreeModel newModel = new DefaultTreeModel(root);

            ApplicationManager.getApplication().invokeLater(() -> {
                this.tree.setModel(newModel);
                this.tree.setRootVisible(true);
                this.tree.revalidate();
                this.tree.repaint();
            });
        });
    }

    private DefaultMutableTreeNode buildNodeRecursive(@NotNull Directory dir, @Nullable String subFolder) {
        System.out.println("TC buildNodeRecursive");

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        File folderToScan = (subFolder != null)
                ? dir.getFilePath().resolve(subFolder).toFile()
                : dir.getFile();

        Optional.ofNullable(folderToScan.listFiles(File::isDirectory))
                .stream()
                .flatMap(Arrays::stream)
                //.parallel()
                .map(TestCaseTabController::map)
                .filter(Objects::nonNull)
                .forEachOrdered(caseDir -> node.add(buildNodeRecursive(caseDir, null)));

        return node;
    }


}