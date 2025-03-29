package com.example.demo;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import javax.swing.*;

public class ShowTestTreeAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        JFrame frame = new JFrame("Test Plan Tree");
        frame.setContentPane(new TestTreePanel().getMainPanel());
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}