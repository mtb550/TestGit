package com.example.demo;

import javax.swing.*;
import java.util.List;

public class TestCaseEditor {
    public static void open(String projectName, Feature feature) {
        JFrame frame = new JFrame(projectName + " - " + feature.getName() + " Test Cases");

        String[] columns = {"Title", "Expected Result", "Steps", "Priority"};
        List<TestCase> cases = DB.loadTestCases(feature.getId());
        String[][] data = new String[cases.size()][4];

        for (int i = 0; i < cases.size(); i++) {
            TestCase tc = cases.get(i);
            data[i][0] = tc.getTitle();
            data[i][1] = tc.getExpectedResult();
            data[i][2] = tc.getSteps();
            data[i][3] = tc.getPriority();
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    TestCase selected = cases.get(row);
                    TestCaseToolWindow.show(selected);
                }
            }
        });

        frame.getContentPane().add(new JScrollPane(table));
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
