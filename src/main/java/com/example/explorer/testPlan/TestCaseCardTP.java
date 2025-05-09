package com.example.explorer.testPlan;

import com.example.pojo.TestCase;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class TestCaseCardTP extends JPanel {
    public TestCaseCardTP(int index, TestCase tc) {
        setLayout(new BorderLayout(10, 10));

        setBackground(index % 2 == 0
                ? new JBColor(Gray._245, Gray._60)  // even row
                : new JBColor(Gray._230, Gray._45)  // odd row

        );
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Gray._60),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JBLabel title = new JBLabel("#" + (index + 1) + ". " + tc.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new JBColor(
                Color.DARK_GRAY,   // for light theme
                Color.LIGHT_GRAY   // for dark theme
        ));

        // Align the component to the left
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JBLabel expected = new JBLabel("Expected: " + tc.getExpectedResult());
        expected.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        expected.setForeground(new JBColor(
                Color.DARK_GRAY,   // for light theme
                Color.LIGHT_GRAY   // for dark theme
        ));
        expected.setAlignmentX(Component.LEFT_ALIGNMENT);

        JBLabel steps = new JBLabel("Steps: " + tc.getSteps());
        steps.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        steps.setForeground(new JBColor(
                Color.DARK_GRAY,   // for light theme
                Color.LIGHT_GRAY   // for dark theme
        ));
        steps.setAlignmentX(Component.LEFT_ALIGNMENT);

        JBLabel automationRef = new JBLabel("Automation Ref: " + tc.getAutomationRef());
        automationRef.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        automationRef.setForeground(new JBColor(
                Color.DARK_GRAY,   // for light theme
                Color.LIGHT_GRAY   // for dark theme
        ));
        automationRef.setAlignmentX(Component.LEFT_ALIGNMENT);

        JBLabel priorityBadge = getJbLabel(tc);

        // Panel for the title and priority side-by-side
        JBPanel<?> titleLine = new JBPanel<>();
        titleLine.setLayout(new BoxLayout(titleLine, BoxLayout.X_AXIS));
        titleLine.setOpaque(false);
        // Ensure the line itself is left-aligned
        titleLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleLine.add(title);
        titleLine.add(Box.createHorizontalStrut(8));
        titleLine.add(priorityBadge);

        // Main content panel stacked vertically
        JBPanel<?> content = new JBPanel<>();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        // Ensure content panel is left-aligned
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components in vertical order
        content.add(titleLine);
        content.add(Box.createVerticalStrut(6));
        content.add(expected);
        content.add(steps);
        content.add(automationRef);

        // Add the content panel to the main panel
        add(content, BorderLayout.CENTER);

        // === Hover Action Buttons Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setOpaque(false);

        JButton btnPassed = createStatusButton("PASSED", new JBColor(
                new Color(0, 128, 0, 180),     // light mode
                new Color(0, 200, 0, 150)      // dark mode
        ));

        JButton btnFailed = createStatusButton("FAILED", new JBColor(
                new Color(200, 0, 0, 180),
                new Color(255, 80, 80, 150)
        ));

        JButton btnBlocked = createStatusButton("BLOCKED", new JBColor(
                new Color(255, 140, 0, 180),
                new Color(255, 165, 0, 150)
        ));


        buttonPanel.add(btnPassed);
        buttonPanel.add(btnFailed);
        buttonPanel.add(btnBlocked);
        buttonPanel.setVisible(false);  // Initially hidden

        add(buttonPanel, BorderLayout.NORTH);

        // === Hover Events to Show/Hide Buttons ===
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                buttonPanel.setVisible(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                buttonPanel.setVisible(false);
            }
        });

    }

    private static @NotNull JBLabel getJbLabel(TestCase tc) {
        JBLabel priorityBadge = new JBLabel(tc.getPriority().toUpperCase());
        priorityBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        priorityBadge.setOpaque(true);
        priorityBadge.setForeground(new JBColor(
                Color.DARK_GRAY,   // for light theme
                Color.LIGHT_GRAY   // for dark theme
        ));
        priorityBadge.setBackground(switch (tc.getPriority().toLowerCase()) {
            case "high" -> JBColor.ORANGE;
            case "medium" -> JBColor.magenta;
            default -> JBColor.green;
        });
        priorityBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        priorityBadge.setHorizontalAlignment(SwingConstants.CENTER);
        priorityBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        return priorityBadge;
    }

    private JButton createStatusButton(String label, JBColor background) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(background);
        button.setForeground(JBColor.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(80, 26));
        return button;
    }


}
