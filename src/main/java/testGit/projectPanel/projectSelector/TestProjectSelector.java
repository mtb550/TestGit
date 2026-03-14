package testGit.projectPanel.projectSelector;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;
import lombok.Setter;
import testGit.pojo.Config;
import testGit.pojo.Project;
import testGit.pojo.ProjectStatus;
import testGit.projectPanel.ProjectPanel;
import testGit.util.DirectoryMapper;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TestProjectSelector {
    private final ProjectPanel projectPanel;
    @Getter
    @Setter
    private DefaultComboBoxModel<Project> testProjectList;
    @Getter
    @Setter
    private ComboBox<Project> selectedTestProject;

    public TestProjectSelector(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        testProjectList = new DefaultComboBoxModel<>();
        selectedTestProject = new ComboBox<>(testProjectList);

        selectedTestProject.setFocusable(false);
        selectedTestProject.setRenderer(new RendererImpl());
        selectedTestProject.addActionListener(new Listener(projectPanel));
    }

    public boolean init() {
        System.out.println("TestProjectSelector.init()");
        boolean status = loadTestProjectList();
        System.out.println("status = " + status);

        return status;
    }

    public boolean loadTestProjectList() {
        System.out.println("TestProjectSelector.loadTestProjectList()");

        testProjectList.removeAllElements();

        File root = Config.getTestGitPath().toFile();

        File[] dirs = root.listFiles(File::isDirectory);

        Optional.ofNullable(dirs)
                .stream()
                .flatMap(Arrays::stream)
                .filter(item -> !item.getName().equals(".git") && item.getName().contains("_"))
                .peek(System.out::println)
                .map(DirectoryMapper::mapProject)
                .filter(Objects::nonNull)
                .filter(p -> p.getProjectStatus() == ProjectStatus.AC)
                .forEach(testProjectList::addElement);

        if (!root.exists() || testProjectList.getSize() == 0) {
            System.out.println("not projects. " + root.exists() + " , " + testProjectList.getSize());
            projectPanel.showEmptyState();
            selectedTestProject.setEnabled(false);
            return false;
        }

        selectedTestProject.setEnabled(true);
        Project firstTestProject = testProjectList.getElementAt(0);
        selectedTestProject.setSelectedItem(firstTestProject);
        //filterByTestProject(firstTestProject);
        return true;
    }

    public void addTestProject(Project newTestProject) {
        System.out.println("TestProjectSelector.addTestProject()");
        if (!selectedTestProject.isEnabled()) {
            projectPanel.showEmptyState();
        }

        testProjectList.addElement(newTestProject);
        selectedTestProject.setSelectedItem(newTestProject);
        if (testProjectList.getSize() == 1) {
            selectedTestProject.setEnabled(true);
            projectPanel.setupMainLayout();
        }
    }

    public void removeTestProject(SimpleTree tree, Project project) {
        int indexToRemove = -1;
        for (int i = 0; i < testProjectList.getSize(); i++) {
            if (testProjectList.getElementAt(i).getName().equals(project.getName())) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) return; // Not found

        ActionListener[] listeners = selectedTestProject.getActionListeners();
        for (ActionListener l : listeners) selectedTestProject.removeActionListener(l);

        try {
            testProjectList.removeElementAt(indexToRemove);

            if (testProjectList.getSize() == 0) {
                testProjectList.removeAllElements();
                projectPanel.showEmptyState();
            } else {
                selectedTestProject.setSelectedItem(testProjectList.getElementAt(Math.max(0, indexToRemove - 1)));
                filterByTestProject(selectedTestProject.getItem());
            }
        } finally {
            for (ActionListener l : listeners) selectedTestProject.addActionListener(l);
        }

        tree.revalidate();
        tree.repaint();
    }

    public void filterByTestProject(Project testProject) {
        System.out.println("Panel.filterByProject(): " + testProject.getName());

        projectPanel.getTestCaseTabController().buildTreeAsync(selectedTestProject.getItem());
        projectPanel.getTestRunTabController().buildTreeAsync(selectedTestProject.getItem());

    }

}