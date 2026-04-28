package org.testin.projectPanel.projectSelector;

import org.testin.pojo.dto.dirs.TestProjectDirectoryDto;
import org.testin.projectPanel.ProjectPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Listener implements ActionListener {
    private final ProjectPanel projectPanel;
    private TestProjectDirectoryDto lastSelected = null;

    public Listener(ProjectPanel projectPanel) {
        this.projectPanel = projectPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComboBox<?> comboBox) {
            if (comboBox.getSelectedItem() instanceof TestProjectDirectoryDto selected) {
                if (selected.equals(lastSelected)) {
                    return;
                }

                lastSelected = selected;

                if (projectPanel.getTestProjectSelector() != null) {
                    System.out.println("Selection changed to: " + selected.getName());
                    projectPanel.getTestProjectSelector().filterByTestProject(selected);
                }
            }
        }
    }
}