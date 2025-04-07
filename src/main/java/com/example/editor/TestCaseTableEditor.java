package com.example.editor;

import com.example.demo.TestCaseToolWindow;
import com.example.pojo.Feature;
import com.example.pojo.TestCase;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class TestCaseTableEditor extends UserDataHolderBase implements FileEditor {
    private final JPanel panel;

    public TestCaseTableEditor(@NotNull Feature feature) {
        panel = new JBPanel<>(new BorderLayout());
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(30, 30, 30)); // Dark-themed background

        List<TestCase> testCases = feature.getTestCases();
        for (int i = 0; i < testCases.size(); i++) {
            TestCaseCard card = new TestCaseCard(i, testCases.get(i));
            listPanel.add(card);
            listPanel.add(Box.createVerticalStrut(8));
        }

        JBScrollPane scrollPane = new JBScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return panel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return panel;
    }

    @Override
    public @NotNull String getName() {
        return "Test Case Cards";
    }

    @Override
    public void dispose() {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    static class TestCaseCard extends JPanel {
        public TestCaseCard(int index, TestCase tc) {
            setLayout(new BorderLayout(10, 10));
            setBackground(index % 2 == 0 ? new Color(40, 40, 40) : new Color(50, 50, 50));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(60, 60, 60)),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

            JLabel title = new JLabel("#" + (index + 1) + ". " + tc.getTitle());
            title.setFont(new Font("Segoe UI", Font.BOLD, 14));
            title.setForeground(Color.WHITE);

            JLabel expected = new JLabel("Expected: " + tc.getExpectedResult());
            expected.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            expected.setForeground(Color.LIGHT_GRAY);

            JLabel steps = new JLabel("Steps: " + tc.getSteps());
            steps.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            steps.setForeground(Color.LIGHT_GRAY);

            JLabel priority = new JLabel("Priority: " + tc.getPriority());
            priority.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            priority.setForeground(Color.LIGHT_GRAY);

            JLabel automationRef = new JLabel("Automation Ref: " + tc.getAutomationRef());
            automationRef.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            automationRef.setForeground(new Color(200, 200, 255));

            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.add(title);
            content.add(Box.createVerticalStrut(4));
            content.add(expected);
            content.add(steps);
            content.add(priority);
            content.add(automationRef);

            add(content, BorderLayout.CENTER);

            JPopupMenu contextMenu = new JPopupMenu();

            JMenuItem copyItem = new JMenuItem("📋 Copy");
            copyItem.addActionListener(evt -> {
                String text = String.format("Title: %s\nSteps: %s\nExpected: %s",
                        tc.getTitle(), tc.getSteps(), tc.getExpectedResult());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(text), null);
            });

            JMenuItem runItem = new JMenuItem("▶ Run Test");
            runItem.addActionListener(evt -> {
                String automation = tc.getAutomationRef();
                if (automation != null && !automation.isBlank()) {
                    com.example.Runner.TestNGRunnerByClassName.runTestClass(
                            com.intellij.openapi.project.ProjectManager.getInstance().getOpenProjects()[0],
                            automation);
                }
            });

            JMenuItem viewItem = new JMenuItem("🔍 View Details");
            viewItem.addActionListener(evt -> TestCaseToolWindow.show(tc));

            contextMenu.add(copyItem);
            contextMenu.add(runItem);
            contextMenu.add(viewItem);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        contextMenu.show(TestCaseCard.this, e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) {
                        TestCaseToolWindow.show(tc);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    setBackground(getBackground().brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getDefaultCursor());
                    setBackground(index % 2 == 0 ? new Color(40, 40, 40) : new Color(50, 50, 50));
                }
            });
        }
    }
}
