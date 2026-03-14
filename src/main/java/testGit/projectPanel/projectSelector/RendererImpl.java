package testGit.projectPanel.projectSelector;

import testGit.pojo.TestProject;

import javax.swing.*;
import java.awt.*;

public class RendererImpl extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof TestProject pr) {
            setText(pr.getName());
        }

        return this;
    }
}