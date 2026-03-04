package testGit.projectPanel.projectSelector;

import com.intellij.openapi.ui.ComboBox;
import lombok.Getter;
import lombok.Setter;
import testGit.pojo.Config;
import testGit.pojo.Directory;
import testGit.projectPanel.ProjectPanel;
import testGit.projectPanel.testCaseTab.TestCaseRenderer;
import testGit.projectPanel.testRunTab.TestRunRenderer;
import testGit.util.TestCasesDirectoryMapper;
import testGit.util.TestRunsDirectoryMapper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TestProjectSelector {
    private final ProjectPanel projectPanel;
    @Getter
    @Setter
    private DefaultComboBoxModel<Directory> testProjectList;
    @Getter
    @Setter
    private ComboBox<Directory> selectedTestProject;

    public TestProjectSelector(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        testProjectList = new DefaultComboBoxModel<>();
        selectedTestProject = new ComboBox<>(testProjectList);

        selectedTestProject.setFocusable(false);
        selectedTestProject.setRenderer(new Renderer());
        selectedTestProject.addActionListener(new Listener(projectPanel));
    }

    public void init() {
        loadTestProjectList();
        Directory allProjects = testProjectList.getElementAt(0);

        TestCasesDirectoryMapper.buildTreeAsync(allProjects, projectPanel.getTestCaseTabController().getTree());
        TestRunsDirectoryMapper.buildTreeAsync(allProjects, projectPanel.getTestRunTabController().getTree());

        if (projectPanel.getTestCaseTabController().getTree().getCellRenderer() == null ||
                !(projectPanel.getTestCaseTabController().getTree().getCellRenderer() instanceof TestCaseRenderer)) {
            projectPanel.getTestCaseTabController().init();
        }

        if (projectPanel.getTestRunTabController().getTree().getCellRenderer() == null ||
                !(projectPanel.getTestRunTabController().getTree().getCellRenderer() instanceof TestRunRenderer)) {
            projectPanel.getTestRunTabController().init();
        }

        projectPanel.getTestCaseTabController().getTree().setRootVisible(true);
        projectPanel.getTestRunTabController().getTree().setRootVisible(true);

        projectPanel.getTestCaseTabController().getTree().repaint();
        projectPanel.getTestRunTabController().getTree().repaint();
    }

    public void loadTestProjectList() {
        testProjectList.removeAllElements();

        File root = Config.getTestGitPath().toFile();

        File[] dirs = root.listFiles(File::isDirectory);

        Optional.ofNullable(dirs)
                .stream()
                .flatMap(Arrays::stream)
                .filter(item -> !item.getName().equals(".git") && item.getName().contains("_"))
                .parallel()
                .map(TestCasesDirectoryMapper::map)
                .filter(Objects::nonNull)
                .filter(p -> p.getActive() == 1)
                .forEach(testProjectList::addElement);

        if (!root.exists() || testProjectList.getSize() == 0) {
            // show no test projects
            // make project disabled
            //selectedTestProject.setSelectedIndex(0);
            //selectedTestProject.setEnabled(false);
            //return;
            selectedTestProject.setEnabled(false);
            projectPanel.getTestCaseTabController().getTree().getEmptyText().setText("No projects found");
            return;
        }

        Directory allProjects = new Directory().setName("All Projects");
        testProjectList.insertElementAt(allProjects, 0);

        selectedTestProject.setSelectedItem(allProjects);
        selectedTestProject.setEnabled(true);
    }

    public void addTestProject(Directory testProject) {
        if (!selectedTestProject.isEnabled()) {
            selectedTestProject.setEnabled(true);
        }
        testProjectList.addElement(testProject);
    }

    public void filterByTestProject(Directory testProject, ProjectPanel projectPanel) {
        System.out.println("Panel.filterByProject(): " + testProject.getName());

        if (testProject.getName().equals("All Projects")) {
            TestCasesDirectoryMapper.buildTreeAsync(getSelectedTestProject().getItem(), projectPanel.getTestCaseTabController().getTree());
            TestRunsDirectoryMapper.buildTreeAsync(getSelectedTestProject().getItem(), projectPanel.getTestRunTabController().getTree());
        } else {
            DefaultMutableTreeNode casesRoot = TestCasesDirectoryMapper.buildNodeRecursive(testProject, "testCases");
            DefaultMutableTreeNode runsRoot = TestRunsDirectoryMapper.buildNodeRecursive(testProject, "testRuns");

            projectPanel.getTestCaseTabController().getTree().setModel(new DefaultTreeModel(casesRoot));
            projectPanel.getTestRunTabController().getTree().setModel(new DefaultTreeModel(runsRoot));
        }

        if (projectPanel.getTestCaseTabController().getTree().getCellRenderer() == null ||
                !(projectPanel.getTestCaseTabController().getTree().getCellRenderer() instanceof TestCaseRenderer)) {
            projectPanel.getTestCaseTabController().init();
        }

        if (projectPanel.getTestRunTabController().getTree().getCellRenderer() == null ||
                !(projectPanel.getTestRunTabController().getTree().getCellRenderer() instanceof TestRunRenderer)) {
            projectPanel.getTestRunTabController().init();
        }

        projectPanel.getTestCaseTabController().getTree().setRootVisible(true);
        projectPanel.getTestRunTabController().getTree().setRootVisible(true);

        projectPanel.getTestCaseTabController().getTree().repaint();
        projectPanel.getTestRunTabController().getTree().repaint();
    }

    /*public void filterByTestProject(Directory testProject, ProjectPanel projectPanel) {
        if (testProject == null) return;

        SimpleTree caseTree = projectPanel.getTestCaseTabController().getTree();
        SimpleTree runTree = projectPanel.getTestRunTabController().getTree();

        if ("All Projects".equals(testProject.getName())) {
            TestCasesDirectoryMapper.buildTreeAsync(testProject.getName(),caseTree);
            TestRunsDirectoryMapper.buildTreeAsync(testProject.getName(),runTree);
        } else {
            // OPTIMIZATION: Try to find the node already in the "All Projects" tree
            DefaultMutableTreeNode existingCaseNode = findNodeByDirectory(caseTree, testProject);
            DefaultMutableTreeNode existingRunNode = findNodeByDirectory(runTree, testProject);

            if (existingCaseNode != null) {
                // Clone the node so we don't accidentally "pull it out" of the hidden full tree
                caseTree.setModel(new DefaultTreeModel(deepCopyNode(existingCaseNode)));
            } else {
                // Fallback: If not found in memory, then (and only then) scan disk
                caseTree.setModel(new DefaultTreeModel(TestCasesDirectoryMapper.buildNodeRecursive(testProject, "testCases")));
            }

            if (existingRunNode != null) {
                runTree.setModel(new DefaultTreeModel(deepCopyNode(existingRunNode)));
            } else {
                runTree.setModel(new DefaultTreeModel(TestRunsDirectoryMapper.buildNodeRecursive(testProject, "testRuns")));
            }
        }

        // Refresh UI Renderers and Visibility
        // ensureRenderersSet(projectPanel);

        caseTree.setRootVisible(true);
        runTree.setRootVisible(true);
    }

    //Helper to find a node in the current tree model that matches the Directory
    private DefaultMutableTreeNode findNodeByDirectory(SimpleTree tree, Directory target) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        if (model == null) return null;

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        if (root == null) return null;

        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            if (child.getUserObject() instanceof Directory dir) {
                if (dir.getName().equals(target.getName())) {
                    return child;
                }
            }
        }
        return null;
    }

    // Deep copy to prevent sharing state between the filtered view and the full view
    private DefaultMutableTreeNode deepCopyNode(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode copy = new DefaultMutableTreeNode(node.getUserObject());
        for (int i = 0; i < node.getChildCount(); i++) {
            copy.add(deepCopyNode((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return copy;
    }*/

}