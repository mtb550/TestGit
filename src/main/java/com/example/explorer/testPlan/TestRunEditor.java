package com.example.explorer.testPlan;

import com.example.editor.TestCaseCard;
import com.example.pojo.TestCase;
import com.example.util.sql;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;

import javax.swing.*;

public class TestRunEditor {
    private static final JBTabsImpl tabs = new JBTabsImpl(ProjectManager.getInstance().getOpenProjects()[0]);

    public static void open(int testRunId, String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        TestCase[] testCases = new sql().get("""
                    SELECT * FROM nafath_tc
                    WHERE tc_id IN (
                        SELECT test_case_id FROM nafath_tp_testcases
                        WHERE plan_id = ?
                        ORDER BY run_order
                    )
                """, testRunId).as(TestCase[].class);


        for (int i = 0; i < testCases.length; i++) {
            panel.add(new TestCaseCard(i, testCases[i]));
            panel.add(Box.createVerticalStrut(8));
        }

        JBScrollPane scrollPane = new JBScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        TabInfo tab = new TabInfo(scrollPane).setText("Run: " + title);
        tabs.addTab(tab);
        tabs.select(tab, true);

        JFrame frame = getOrCreateWindow();
        frame.setContentPane(tabs.getComponent());
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private static JFrame getOrCreateWindow() {
        if (tabs.getComponent().getTopLevelAncestor() instanceof JFrame existingFrame) {
            return existingFrame;
        }
        JFrame frame = new JFrame("Test Run Viewer");
        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);
        return frame;
    }
}
