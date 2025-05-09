package com.example.explorer.testPlan;

import com.example.pojo.TestCase;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class TestCaseCardWrapper extends JPanel {

    public TestCaseCardWrapper(int index, TestCase testCase) {
        setLayout(new OverlayLayout(this));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        setOpaque(false);

        // === The actual test case card
        TestCaseCardTP card = new TestCaseCardTP(index, testCase);
        card.setAlignmentX(0f); // Align to left
        card.setAlignmentY(0f);

        // === Floating button panel (BoxLayout with right-aligned buttons)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(0f); // Align top-left for full-width overlay
        buttonPanel.setAlignmentY(0f);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        buttonPanel.setVisible(false);

        buttonPanel.add(Box.createHorizontalGlue()); // push buttons to the right

        JButton btnPassed = createStatusButton("PASSED", new JBColor(new Color(0, 128, 0, 180), new Color(0, 200, 0, 150)));
        JButton btnBlocked = createStatusButton("BLOCKED", new JBColor(new Color(255, 165, 0, 180), new Color(255, 140, 0, 150)));
        JButton btnFailed = createStatusButton("FAILED", new JBColor(new Color(200, 0, 0, 180), new Color(255, 80, 80, 150)));

        buttonPanel.add(btnPassed);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(btnBlocked);
        buttonPanel.add(Box.createHorizontalStrut(6));
        buttonPanel.add(btnFailed);

        // === Add hover listeners on wrapper
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                buttonPanel.setVisible(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                buttonPanel.setVisible(false);
            }
        });

        // === Layered view
        add(buttonPanel); // top
        add(card);        // bottom
    }

    private JButton createStatusButton(String label, JBColor background) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(80, 26));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }
}
