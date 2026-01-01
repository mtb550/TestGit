package com.example.explorer;

import com.example.pojo.Directory;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Arrays;

import static com.example.pojo.Config.rootFolder;

public class ComboBoxProjectSelector {
    private static ComboBox<Directory> comboBox;
    private final DefaultComboBoxModel<Directory> model;
    public ExplorerPanel panel;

    public ComboBoxProjectSelector(final ExplorerPanel panel) {
        this.panel = panel;
        model = new DefaultComboBoxModel<>();
        comboBox = new ComboBox<>(model);
        comboBox.setFocusable(false);
        loadModel();
    }

    public static Directory getSelectedProject() {
        return (Directory) comboBox.getSelectedItem();
    }

    public void loadModel() {
        File[] dirs = rootFolder.listFiles(File::isDirectory);

        Directory[] projects = (dirs == null) ? new Directory[0] : Arrays.stream(dirs)
                .map(dir -> {
                    String fullName = dir.getName();
                    String[] parts = fullName.split("_", 4);

                    return new Directory()
                            .setFile(dir)
                            .setFileName(fullName)
                            .setFilePath(rootFolder.toPath().resolve(fullName))
                            .setType(Integer.parseInt(parts[0]))
                            .setId(Integer.parseInt(parts[1]))
                            .setName(parts[2])
                            .setActive(Integer.parseInt(parts[3]));

                })
                .filter(p -> p.getActive() == 1)
                .toArray(Directory[]::new);

        // Sort alphabetically
        //java.util.Arrays.sort(projects);

        // Now you have: ["ibram", "nafath", ...]
        System.out.println("Found projects: " + Arrays.toString(projects));

        if (projects.length > 0) {
            for (Directory project : projects) {
                model.addElement(project);
            }

            comboBox.addActionListener(this::onSelection);
            comboBox.setSelectedIndex(0);
        } else {
            //comboBox.addItem("No projects found");
            comboBox.setEnabled(false);
        }

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(((Directory) value).getName()); // هنا نخبره أن يعرض حقل الـ Name فقط
                return this;
            }
        });
    }

    public void addAndSelectProject(Directory project) {
        if (!comboBox.isEnabled()) {
            comboBox.setEnabled(true);
        }
        model.addElement(project);
        comboBox.setSelectedItem(project); // This triggers focus/selection
    }

    private void onSelection(ActionEvent e) {
        Directory selected = (Directory) comboBox.getSelectedItem();
        if (selected != null)
            panel.filterByProject(selected);

    }

    public JComponent getComponent() {
        return comboBox;
    }


}
