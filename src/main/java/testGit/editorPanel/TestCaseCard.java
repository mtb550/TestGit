package testGit.editorPanel;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.*;

public class TestCaseCard extends JPanel {
    public TestCaseCard(int index, TestCase tc) {
        // Use JBUI.Borders for proper scaling on High-DPI screens
        setLayout(new BorderLayout(JBUI.scale(12), JBUI.scale(12)));

        // Theme-aware background alternating
        setBackground(index % 2 == 0
                ? new JBColor(Gray._245, Gray._60)
                : new JBColor(Gray._230, Gray._45)
        );

        // Compound border using IDE standard line colors
        setBorder(JBUI.Borders.compound(
                JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0), // Top/Bottom border only
                JBUI.Borders.empty(12)
        ));

        setMaximumSize(new Dimension(Integer.MAX_VALUE, JBUI.scale(160)));

        // --- TITLE SECTION ---
        // Using FontSize.BIGGER to make the title prominent as requested
        JBLabel title = new JBLabel("#" + (index + 1) + ". " + tc.getTitle());
        title.setFont(UIUtil.getLabelFont(UIUtil.FontSize.NORMAL).deriveFont(Font.BOLD));
        title.setForeground(UIUtil.getLabelForeground());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- DETAILS SECTION ---
        // Use getContextHelpForeground or getInactiveTextColor for secondary info
        JBLabel expected = createDetailLabel("Expected: " + tc.getExpectedResult(), false);
        JBLabel steps = createDetailLabel("Steps: " + tc.getSteps(), false);
        JBLabel automationRef = createDetailLabel("Automation Ref: " + tc.getAutomationRef(), true);

        JBLabel priorityBadge = getPriorityBadge(tc);

        // Layout: Title and Badge
        JBPanel<?> titleLine = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, JBUI.scale(10), 0));
        titleLine.setOpaque(false);
        titleLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLine.add(title);
        titleLine.add(priorityBadge);

        // Layout: Vertical Content Stack
        JBPanel<?> content = new JBPanel<>();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLine);
        content.add(Box.createVerticalStrut(JBUI.scale(8)));
        content.add(expected);
        content.add(steps);
        content.add(Box.createVerticalStrut(JBUI.scale(4)));
        content.add(automationRef);

        add(content, BorderLayout.CENTER);
    }

    private static @NotNull JBLabel getPriorityBadge(TestCase tc) {
        JBLabel priorityBadge = new JBLabel(tc.getPriority().toUpperCase());
        // Small bold font for the badge
        priorityBadge.setFont(UIUtil.getLabelFont(UIUtil.FontSize.SMALL).deriveFont(Font.BOLD));
        priorityBadge.setOpaque(true);

        // Ensure contrast against the colored background
        priorityBadge.setForeground(JBColor.WHITE);

        priorityBadge.setBackground(switch (tc.getPriority().toLowerCase()) {
            case "high" -> JBColor.RED; // Red is more standard for High
            case "medium" -> JBColor.BLUE;
            default -> new JBColor(new Color(40, 167, 69), new Color(40, 167, 69)); // Forest Green
        });

        priorityBadge.setBorder(JBUI.Borders.empty(2, 8));
        priorityBadge.setHorizontalAlignment(SwingConstants.CENTER);
        return priorityBadge;
    }

    private JBLabel createDetailLabel(String text, boolean italic) {
        JBLabel label = new JBLabel(text);
        // Use standard label font but slightly larger than default
        label.setFont(UIUtil.getLabelFont(UIUtil.FontSize.NORMAL));
        label.setForeground(UIUtil.getContextHelpForeground()); // Better for different themes
        if (italic) {
            label.setFont(label.getFont().deriveFont(Font.ITALIC));
        }
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}