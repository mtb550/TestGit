package testGit.projectPanel.projectSelector;

import testGit.pojo.Directory;

import javax.swing.*;
import java.awt.*;

public class RendererImpl extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof Directory dir) {
            setText(dir.getName());
        }

        return this;
    }
}