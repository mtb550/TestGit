package testGit.projectPanel.projectSelector;

import com.intellij.openapi.ui.ComboBox;
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

public class ProjectSelector {
    private final ComboBox<Directory> comboBox;
    private final DefaultComboBoxModel<Directory> comboBoxModel;

    public ProjectSelector(ProjectPanel projectPanel) {
        comboBoxModel = new DefaultComboBoxModel<>();
        comboBox = new ComboBox<>(comboBoxModel);
        comboBox.setFocusable(false);

        comboBox.setRenderer(new Renderer());
        comboBox.addActionListener(new Listener(projectPanel));
    }

    public Directory getSelectedProject() {
        return (Directory) comboBox.getSelectedItem();
    }

    public void loadProjectList() {
        comboBoxModel.removeAllElements();
        Directory allProjects = new Directory().setName("All Projects");
        comboBoxModel.addElement(allProjects);

        File root = Config.getTestGitPath().toFile();
        if (!root.exists()) {
            comboBox.setSelectedIndex(0);
            comboBox.setEnabled(true);
            return;
        }

        File[] dirs = root.listFiles(File::isDirectory);
        Optional.ofNullable(dirs)
                .stream()
                .flatMap(Arrays::stream)
                .filter(item -> !item.getName().equals(".git") && item.getName().contains("_"))
                .parallel()
                .map(TestCasesDirectoryMapper::map)
                .filter(Objects::nonNull)
                .filter(p -> p.getActive() == 1)
                .forEach(comboBoxModel::addElement);

        comboBox.setEnabled(comboBoxModel.getSize() > 0);
        comboBox.setSelectedIndex(0);
    }

    public JComboBox<Directory> selected() {
        return comboBox;
    }

    public void selectProject(Directory testProject) {
        comboBox.setSelectedItem(testProject);
    }

    public void addProject(Directory testProject) {
        if (!comboBox.isEnabled()) {
            comboBox.setEnabled(true);
        }

        comboBoxModel.addElement(testProject);
    }

    public void filterByTestProject(Directory testProject, ProjectPanel projectPanel) {
        System.out.println("Panel.filterByProject(): " + testProject.getName());

        if (testProject.getName().equals("All Projects")) {
            TestCasesDirectoryMapper.buildTreeAsync(getSelectedProject(), projectPanel.getTestCaseTabController().getTree());
            TestRunsDirectoryMapper.buildTreeAsync(getSelectedProject(), projectPanel.getTestRunTabController().getTree());
        } else {
            DefaultMutableTreeNode casesRoot = TestCasesDirectoryMapper.buildNodeRecursive(testProject, "testCases");
            DefaultMutableTreeNode runsRoot = TestRunsDirectoryMapper.buildNodeRecursive(testProject, "testRuns");

            projectPanel.getTestCaseTabController().getTree().setModel(new DefaultTreeModel(casesRoot));
            projectPanel.getTestRunTabController().getTree().setModel(new DefaultTreeModel(runsRoot));
        }

        if (projectPanel.getTestCaseTabController().getTree().getCellRenderer() == null ||
                !(projectPanel.getTestCaseTabController().getTree().getCellRenderer() instanceof TestCaseRenderer)) {
            projectPanel.getTestCaseTabController().setup();
        }

        if (projectPanel.getTestRunTabController().getTree().getCellRenderer() == null ||
                !(projectPanel.getTestRunTabController().getTree().getCellRenderer() instanceof TestRunRenderer)) {
            projectPanel.getTestRunTabController().setup();
        }

        projectPanel.getTestCaseTabController().getTree().setRootVisible(true);
        projectPanel.getTestRunTabController().getTree().setRootVisible(true);

        projectPanel.getTestCaseTabController().getTree().repaint();
        projectPanel.getTestRunTabController().getTree().repaint();
    }

}