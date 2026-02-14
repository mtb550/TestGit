package testGit.projectPanel;

import com.intellij.openapi.ui.ComboBox;
import testGit.pojo.Config;
import testGit.pojo.Directory;
import testGit.util.DirectoryMapper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class ProjectSelector {
    public static ComboBox<Directory> comboBox;
    private final DefaultComboBoxModel<Directory> model;
    public ProjectPanel projectPanel;

    public ProjectSelector(final ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
        this.model = new DefaultComboBoxModel<>();
        comboBox = new ComboBox<>(model);
        comboBox.setFocusable(false);

        // ✅ 1. الإعدادات الثابتة توضع هنا لمرة واحدة فقط
        setupRenderer();
        setupSelectionListener();

        // 2. تحميل البيانات
        loadProjectList();
    }

    public static Directory getSelectedProject() {
        return (Directory) comboBox.getSelectedItem();
    }

    private void setupRenderer() {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Directory dir) {
                    setText(dir.getName());
                } else if (model.getSize() == 0) {
                    setText("No projects found");
                }
                return this;
            }
        });
    }

    private void setupSelectionListener() {
        comboBox.addActionListener(e -> {
            Directory selected = (Directory) comboBox.getSelectedItem();
            if (selected != null) {
                System.out.println("Selection changed to: " + selected.getName());
                projectPanel.filterByProject(selected);
            }
        });
    }

    public void loadProjectList() {
        System.out.println("ComboBoxProjectSelector.loadProjects()");

        model.removeAllElements();

        File root = Config.getRootFolderFile();
        File[] dirs = root.listFiles(File::isDirectory);

        Directory allProjects = new Directory().setName("All Projects");
        model.addElement(allProjects);

        if (dirs != null) {
            Arrays.stream(dirs)
                    .filter(dir -> !dir.getName().equals(".git") && dir.getName().contains("_"))
                    .map(DirectoryMapper::map)
                    .filter(p -> p != null && p.getActive() == 1)
                    .forEach(model::addElement);
        }

        comboBox.setEnabled(model.getSize() > 0);
        comboBox.setSelectedIndex(0);
    }

    public JComboBox<Directory> selected() {
        return comboBox;
    }

    public void addAndSelectProject(Directory project) {
        System.out.println("ComboBoxProjectSelector.addAndSelectProject()");

        if (!comboBox.isEnabled()) {
            comboBox.setEnabled(true);
        }

        model.addElement(project);
        comboBox.setSelectedItem(project);
    }
}