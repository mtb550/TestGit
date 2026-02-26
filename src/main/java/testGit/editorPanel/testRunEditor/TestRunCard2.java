package testGit.editorPanel.testRunEditor;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import testGit.pojo.GroupType;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TestRunCard2 extends JBPanel<TestRunCard2> {
    private final JBLabel titleLabel = new JBLabel();
    private final JBPanel<?> badgePanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, JBUI.scale(10), 0));
    private final JBLabel expectedLabel = createDetailLabel();
    private final JBLabel stepsLabel = createDetailLabel();
    private final JBLabel automationRefLabel = createDetailLabel();

    // Panel for the action buttons
    private final JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, JBUI.scale(15), 10));

    public TestRunCard2(int index, TestCase tc) {
        setLayout(new BorderLayout());
        setOpaque(true);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, JBUI.scale(160)));

        Font titleFont = JBFont.label().deriveFont(Font.BOLD, UIUtil.getLabelFont().getSize() + 6.0f);
        titleLabel.setFont(titleFont);
        titleLabel.setText(tc.getTitle());
        titleLabel.setForeground(UIUtil.getLabelForeground());
        badgePanel.setOpaque(false);

        JBPanel<?> titleLine = new JBPanel<>(new BorderLayout());
        titleLine.setOpaque(false);
        titleLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleLine.add(titleLabel, BorderLayout.WEST);
        titleLine.add(badgePanel, BorderLayout.CENTER);

        // Main text content
        JBPanel<?> content = new JBPanel<>();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLine);
        content.add(Box.createVerticalStrut(JBUI.scale(8)));
        content.add(expectedLabel);
        content.add(stepsLabel);
        content.add(Box.createVerticalStrut(JBUI.scale(4)));
        content.add(automationRefLabel);

        // --- Buttons Setup (Passed, Failed, Blocked) ---
        actionButtonPanel.setOpaque(false);
        actionButtonPanel.setVisible(false); // Invisible until hover

        actionButtonPanel.add(createStatusButton("PASSED", new JBColor(new Color(73, 156, 73), new Color(60, 120, 60))));
        actionButtonPanel.add(createStatusButton("FAILED", new JBColor(new Color(188, 63, 60), new Color(140, 40, 40))));
        actionButtonPanel.add(createStatusButton("BLOCKED", JBColor.ORANGE));

        // Wrapper to hold everything (Same hierarchy as your request)
        JBPanel<?> wrapper = new JBPanel<>(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(JBUI.Borders.empty(12));

        wrapper.add(content, BorderLayout.CENTER);
        wrapper.add(actionButtonPanel, BorderLayout.EAST); // Place buttons at the end

        add(wrapper, BorderLayout.CENTER);

        // --- Hover Listener to show/hide buttons ---
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                actionButtonPanel.setVisible(true);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Check if mouse actually left the component area
                if (!getBounds().contains(e.getPoint())) {
                    actionButtonPanel.setVisible(false);
                    repaint();
                }
            }
        });

        // Call it right here!
        updateData(index, tc);
    }

    private JButton createStatusButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.putClientProperty("JButton.buttonType", "roundRect"); // Professional IntelliJ style
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(JBFont.small().asBold());
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
    }

    public void updateData(int index, TestCase tc) {
        titleLabel.setText((index + 1) + ". " + tc.getTitle());
        expectedLabel.setText("Expected Result: " + tc.getExpectedResult());
        stepsLabel.setText("Steps: " + tc.getSteps());
        automationRefLabel.setText("Automation Reference: " + tc.getAutomationRef());

        setBackground(index % 2 == 0 ? new JBColor(Gray._245, Gray._60) : new JBColor(Gray._230, Gray._45));
        // Use a bottom border line for a cleaner professional list look
        setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0));

        badgePanel.removeAll();
        badgePanel.add(createPriorityBadge(tc));
        List<GroupType> groups = tc.getGroups();
        if (groups != null) {
            for (GroupType groupName : groups) {
                badgePanel.add(createGroupBadge(groupName));
            }
        }
    }

    private JBLabel createPriorityBadge(TestCase tc) {
        Color bg = switch (tc.getPriority().toLowerCase()) {
            case "high" -> JBColor.CYAN;
            case "medium" -> JBColor.magenta;
            default -> JBColor.lightGray;
        };
        return new RoundedBadge(tc.getPriority(), bg, 20);
    }

    private JBLabel createGroupBadge(GroupType groupName) {
        return new RoundedBadge(groupName.name(), JBColor.darkGray, 20);
    }

    private JBLabel createDetailLabel() {
        JBLabel label = new JBLabel();
        label.setFont(UIUtil.getLabelFont(UIUtil.FontSize.NORMAL));
        label.setForeground(UIUtil.getContextHelpForeground());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    // Professional Rounded Badge (Same as provided)
    private static class RoundedBadge extends JBLabel {
        private final int radius;

        RoundedBadge(String text, Color bg, int radius) {
            super(text.toUpperCase());
            this.radius = radius;
            setOpaque(false);
            setBackground(bg);
            setForeground(JBColor.WHITE);
            setFont(UIUtil.getLabelFont(UIUtil.FontSize.SMALL).deriveFont(Font.BOLD));
            setBorder(JBUI.Borders.empty(2, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}