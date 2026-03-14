package testGit.projectPanel.testRunTab;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.ui.tree.TreeUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import testGit.actions.CreateTestRunItems;
import testGit.pojo.TestPackage;
import testGit.pojo.TestProject;
import testGit.projectPanel.ProjectPanel;
import testGit.util.DirectoryMapper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TestRunTabController {
    @Getter
    public final SimpleTree tree;
    private final ProjectPanel projectPanel;
    @Getter
    private DefaultMutableTreeNode rootNode;

    public TestRunTabController(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.tree = new SimpleTree();
    }

    public void init() {
        System.out.println("TestRunTabController.init()");

        tree.setCellRenderer(new TestRunRenderer());
        tree.addMouseListener(new MouseAdapterImpl(projectPanel));
        ShortcutHandler.register(projectPanel, tree);

        showEmptyState();
    }

    private void showEmptyState() {
        tree.getEmptyText().clear();

        tree.getEmptyText().appendLine("Create new item", SimpleTextAttributes.LINK_ATTRIBUTES,
                e -> new CreateTestRunItems(projectPanel, tree).actionPerformed(null));

//        tree.getEmptyText().appendLine("Create new package", SimpleTextAttributes.LINK_ATTRIBUTES,
//                e -> new CreateTestRunPackage(tree).actionPerformed(null));

//        tree.getEmptyText().appendLine("Create new test run", SimpleTextAttributes.LINK_ATTRIBUTES,
//                e -> new CreateTestRun(projectPanel).actionPerformed(null));
    }

    public void buildTreeAsync(TestProject selectedTestProject) {
        System.out.println("TestRunTabController.buildTreeAsync()");

        DefaultMutableTreeNode localRoot = new DefaultMutableTreeNode("TEST RUNS");
        File testRunsFolder = selectedTestProject.getFilePath().resolve("testRuns").toFile();


        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (testRunsFolder.exists() && testRunsFolder.isDirectory()) {
                File[] files = testRunsFolder.listFiles();
                if (files != null) {
                    Arrays.stream(files)
                            .map(DirectoryMapper::map)
                            .filter(Objects::nonNull)
                            .forEachOrdered(runDir -> localRoot.add(buildNodeRecursive(runDir)));
                }
            }

            ApplicationManager.getApplication().invokeLater(() -> {
                this.rootNode = localRoot;

                DefaultTreeModel newModel = new DefaultTreeModel(localRoot);
                tree.setModel(newModel);

                tree.setRootVisible(localRoot.getChildCount() > 0);
                tree.setShowsRootHandles(true);
                tree.setDragEnabled(true);
                tree.setDropMode(DropMode.ON_OR_INSERT);
                //tree.setEnabled(true);
                TreeUtil.promiseExpandAll(tree);
                //tree.revalidate();
                //tree.repaint();
            });
        });
    }

    private DefaultMutableTreeNode buildNodeRecursive(@NotNull TestPackage dir) {
        System.out.println("TR buildNodeRecursive");

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);

        Optional.ofNullable(dir.getFile().listFiles())
                .stream()
                .flatMap(Arrays::stream)
                //.parallel()
                .map(DirectoryMapper::map)
                .filter(Objects::nonNull)
                .forEachOrdered(runDir -> node.add(buildNodeRecursive(runDir)));

        return node;
    }

}