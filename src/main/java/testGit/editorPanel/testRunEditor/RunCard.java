package testGit.editorPanel.testRunEditor;

import com.intellij.ui.ColorUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.components.BorderLayoutPanel;
import testGit.editorPanel.Shared;
import testGit.pojo.GroupType;
import testGit.pojo.dto.TestCaseDto;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class RunCard extends JBPanel<RunCard> {

    private static final int CARD_HEIGHT = 130;
    private final JBLabel titleLabel = new JBLabel();
    private final JBPanel<?> badgePanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, JBUI.scale(6), 0));
    private final JBLabel expectedLabel = createDetailLabel();
    private final JBLabel stepsLabel = createDetailLabel();
    private final JBLabel automationRefLabel = createDetailLabel();

    private final JBPanel<?> actionPanel = new JBPanel<>();
    private final JBLabel passedBtn = createActionLabel("PASSED");
    private final JBLabel failedBtn = createActionLabel("FAILED");
    private final JBLabel blockedBtn = createActionLabel("BLOCKED");

    private Color currentRowColor;

    public RunCard() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, JBUI.scale(CARD_HEIGHT)));

        titleLabel.setFont(JBFont.label().deriveFont(Font.BOLD, UIUtil.getLabelFont().getSize() + 10.0f));
        titleLabel.setForeground(UIUtil.getLabelForeground());
        badgePanel.setOpaque(false);

        // 🌟 تجهيز سطر العنوان والأزرار أفقياً
        JBPanel<?> titleLine = new JBPanel<>();
        titleLine.setLayout(new BoxLayout(titleLine, BoxLayout.X_AXIS));
        titleLine.setOpaque(false);
        titleLine.setAlignmentX(Component.LEFT_ALIGNMENT);

        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        actionPanel.setOpaque(false);
        actionPanel.add(passedBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        actionPanel.add(failedBtn);
        actionPanel.add(Box.createRigidArea(new Dimension(8, 0)));
        actionPanel.add(blockedBtn);
        actionPanel.setVisible(false);

        titleLine.add(titleLabel);
        titleLine.add(Box.createRigidArea(new Dimension(10, 0)));
        titleLine.add(actionPanel);

        badgePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JBPanel<?> content = new JBPanel<>(new VerticalLayout(JBUI.scale(4)));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLine);
        content.add(badgePanel);
        content.add(expectedLabel);
        content.add(stepsLabel);
        content.add(automationRefLabel);

        BorderLayoutPanel wrapper = new BorderLayoutPanel();
        wrapper.setOpaque(false);
        wrapper.setBorder(JBUI.Borders.empty(12, 16));
        wrapper.addToCenter(content);

        add(wrapper, BorderLayout.CENTER);
    }

    public void updateData(int index, TestCaseDto tc, boolean showGroups, boolean showPriority, Set<String> activeDetails) {
        titleLabel.setText(String.format("%d. %s", index + 1, tc.getTitle()));
        expectedLabel.setText("Expected Result: " + tc.getExpected());
        stepsLabel.setText("Steps: " + tc.getSteps());
        automationRefLabel.setText("Automation Reference: " + tc.getAutoRef());

        expectedLabel.setVisible(activeDetails.contains("Expected Result"));
        stepsLabel.setVisible(activeDetails.contains("Steps"));
        automationRefLabel.setVisible(activeDetails.contains("Automation Ref"));

        currentRowColor = index % 2 == 0 ? new JBColor(Gray._245, Gray._60) : new JBColor(Gray._230, Gray._45);
        setBackground(currentRowColor);
        setBorder(JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0));

        badgePanel.removeAll();
        if (showPriority) badgePanel.add(Shared.createPriorityBadge(tc));
        if (showGroups && tc.getGroups() != null)
            for (GroupType group : tc.getGroups())
                badgePanel.add(Shared.createGroupBadge(group));
        badgePanel.revalidate();
        badgePanel.repaint();
    }

    public void setActionsState(boolean showActions, String hoveredAction) {
        if (actionPanel.isVisible() != showActions) {
            actionPanel.setVisible(showActions);
        }

        if (showActions) {
            Color passBg = new JBColor(new Color(39, 174, 96, 150), new Color(46, 125, 50, 150));
            Color failBg = new JBColor(new Color(192, 57, 43, 150), new Color(183, 28, 28, 150));
            Color blockBg = new JBColor(new Color(243, 156, 18, 150), new Color(237, 108, 2, 150));

            passedBtn.setBackground("PASSED".equals(hoveredAction) ? ColorUtil.brighter(passBg, 2) : passBg);
            failedBtn.setBackground("FAILED".equals(hoveredAction) ? ColorUtil.brighter(failBg, 2) : failBg);
            blockedBtn.setBackground("BLOCKED".equals(hoveredAction) ? ColorUtil.brighter(blockBg, 2) : blockBg);
        }
    }

    private JBLabel createActionLabel(String text) {
        JBLabel lbl = new JBLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setFont(JBFont.regular().asBold());
        lbl.setForeground(JBColor.WHITE);
        lbl.setBorder(JBUI.Borders.empty(4, 10)); // مساحة داخلية للزر
        return lbl;
    }

    private JBLabel createDetailLabel() {
        JBLabel label = new JBLabel();
        label.setFont(UIUtil.getLabelFont(UIUtil.FontSize.NORMAL));
        label.setForeground(UIUtil.getContextHelpForeground());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}