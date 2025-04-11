package com.example.demo;

import com.example.pojo.DB;
import com.example.pojo.TestCase;
import com.example.pojo.TestCaseHistory;
import com.intellij.ui.components.*;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public class TestCaseToolWindowContent {
    @Getter
    private final JBPanel<?> panel;
    private final JBTabbedPane tabbedPane;
    private final JBPanel<?> detailTab;
    private final JBPanel<?> historyTab;

    public TestCaseToolWindowContent() {
        panel = new JBPanel<>(new BorderLayout());
        tabbedPane = new JBTabbedPane();

        detailTab = new JBPanel<>(new GridLayout(4, 1));
        historyTab = new JBPanel<>(new BorderLayout());

        tabbedPane.addTab("Details", detailTab);
        tabbedPane.addTab("History", historyTab);

        panel.add(tabbedPane, BorderLayout.CENTER);
    }

    public void update(TestCase testCase) {
        detailTab.removeAll();
        historyTab.removeAll();

        detailTab.add(new JBLabel("Title: " + testCase.getTitle()));
        detailTab.add(new JBLabel("Expected: " + testCase.getExpectedResult()));
        detailTab.add(new JBLabel("Steps: " + testCase.getSteps()));
        detailTab.add(new JBLabel("Priority: " + testCase.getPriority()));

        DefaultListModel<String> model = new DefaultListModel<>();
        for (TestCaseHistory history : DB.loadTestCaseHistory()) {
            model.addElement(history.getTimestamp() + " - " + history.getChangeSummary());
        }
        JBList<String> historyList = new JBList<>(model);
        historyTab.add(new JBScrollPane(historyList), BorderLayout.CENTER);

        tabbedPane.setSelectedIndex(0);
        panel.revalidate();
        panel.repaint();
    }

}