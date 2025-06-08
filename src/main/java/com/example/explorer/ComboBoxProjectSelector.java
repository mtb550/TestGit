package com.example.explorer;

import com.example.pojo.Projects;
import com.example.util.sql;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

public class ComboBoxProjectSelector {
    private final ExplorerPanel panel;
    private final ComboBox<String> comboBox;
    private final DefaultComboBoxModel<String> model;
    private final Map<String, Integer> nameToId = new HashMap<>();

    public ComboBoxProjectSelector(ExplorerPanel panel) {
        this.panel = panel;
        this.model = new DefaultComboBoxModel<>();
        this.comboBox = new ComboBox<>(model);
        comboBox.setFocusable(false);

        Projects[] projects = new sql().get("SELECT * FROM projects WHERE active = 1 ORDER BY name").as(Projects[].class);
        if (projects.length > 0) {
            for (Projects project : projects) {
                model.addElement(project.getName());
                nameToId.put(project.getName(), project.getId());
            }
            comboBox.addActionListener(this::onSelection);
            comboBox.setSelectedIndex(0);
        } else {
            comboBox.addItem("No projects found");
            comboBox.setEnabled(false);
        }
    }

    private void onSelection(ActionEvent e) {
        String selected = (String) comboBox.getSelectedItem();
        if (selected != null && nameToId.containsKey(selected)) {
            panel.filterByProject(nameToId.get(selected));
        }
    }

    public JComponent getComponent() {
        return comboBox;
    }

    public int getSelectedProjectId() {
        String selected = (String) comboBox.getSelectedItem();
        return nameToId.getOrDefault(selected, -1);
    }
}
