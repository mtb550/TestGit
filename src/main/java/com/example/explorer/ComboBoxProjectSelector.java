package com.example.explorer;

import com.example.pojo.Tree;
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

        // Load only actual projects (type = 0)
        Tree[] projects = new sql().get("SELECT * FROM tree WHERE type = 0").as(Tree[].class);
        for (Tree project : projects) {
            model.addElement(project.getName());
            nameToId.put(project.getName(), project.getId());
        }

        comboBox.addActionListener(this::onSelection);

        // Select first project by default
        if (model.getSize() > 0) {
            comboBox.setSelectedIndex(0); // triggers onSelection
        }
    }

    private void onSelection(ActionEvent e) {
        String selected = (String) comboBox.getSelectedItem();
        panel.filterByProject(nameToId.get(selected));
    }

    public JComponent getComponent() {
        return comboBox;
    }
}
