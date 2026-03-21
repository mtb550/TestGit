package testGit.editorPanel.testCaseEditor;

import com.intellij.icons.AllIcons;
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
import java.util.List;
import java.util.Set;

public class TestCard extends JBPanel<TestCard> {
    private static final int CARD_HEIGHT = 130;
    private final JBLabel titleLabel = new JBLabel();
    private final JBPanel<?> badgePanel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT, JBUI.scale(10), 0));
    private final JBLabel expectedLabel = createDetailLabel();
    private final JBLabel stepsLabel = createDetailLabel();
    private final JBLabel automationRefLabel = createDetailLabel();
    private final JBLabel businessRefLabel = createDetailLabel();
    private final JBLabel moduleLabel = createDetailLabel();
    private final JBLabel idLabel = createDetailLabel();
    private final JBLabel navigateIcon = new JBLabel(AllIcons.General.ArrowRight);
    private final JBLabel runIcon = new JBLabel(AllIcons.RunConfigurations.TestState.Run);

    // 🌟 1. اللوحة التي ستحتوي على الأيقونات (ستكون مخفية افتراضياً)
    private final JBPanel<?> actionPanel = new JBPanel<>();

    public TestCard() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, JBUI.scale(CARD_HEIGHT)));

        titleLabel.setFont(JBFont.label().deriveFont(Font.BOLD, UIUtil.getLabelFont().getSize() + 10.0f));
        titleLabel.setForeground(UIUtil.getLabelForeground());
        badgePanel.setOpaque(false);

        JBPanel<?> titleLine = new JBPanel<>(new BorderLayout());
        titleLine.setOpaque(false);
        titleLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLine.add(titleLabel, BorderLayout.WEST);
        badgePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JBPanel<?> content = new JBPanel<>(new VerticalLayout(JBUI.scale(4)));
        content.setOpaque(false);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(titleLine);
        content.add(badgePanel);
        content.add(expectedLabel);
        content.add(stepsLabel);
        content.add(automationRefLabel);
        content.add(businessRefLabel);
        content.add(moduleLabel);
        content.add(idLabel);

        // 🌟 2. تجهيز لوحة الأيقونات (Action Panel)
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        actionPanel.setOpaque(false); // شفافة ليظهر لون الخلفية

        // إعداد أيقونة الانتقال (استخدمنا المتغير العام مباشرة بدون كتابة JBLabel قبله)
        navigateIcon.setToolTipText("Navigate to Code");
        navigateIcon.setOpaque(true); // مهم جداً ليقبل لون الـ Hover
        navigateIcon.setBorder(JBUI.Borders.empty(4, 6)); // مساحة داخلية أنيقة

        // إعداد أيقونة التشغيل
        runIcon.setToolTipText("Run test case");
        runIcon.setOpaque(true); // مهم جداً ليقبل لون الـ Hover
        runIcon.setBorder(JBUI.Borders.empty(4, 6)); // مساحة داخلية أنيقة

        // ترتيب الأيقونات داخل اللوحة مع مسافة دقيقة بينهما
        actionPanel.add(navigateIcon);
        actionPanel.add(Box.createRigidArea(new Dimension(8, 0))); // فاصل مساحته 8 بكسل
        actionPanel.add(runIcon);

        actionPanel.setVisible(false);

        BorderLayoutPanel wrapper = new BorderLayoutPanel();
        wrapper.setOpaque(false);
        wrapper.setBorder(JBUI.Borders.empty(12, 16));
        wrapper.addToCenter(content);

        // 🌟 3. إضافة لوحة الأيقونات إلى أقصى اليمين في الـ wrapper
        wrapper.addToRight(actionPanel);

        add(wrapper, BorderLayout.CENTER);
    }

    public void updateData(final int index, final TestCaseDto tc, final boolean showGroups, final boolean showPriority, final Set<String> activeDetails, final boolean isUnsorted) {
        titleLabel.setText((index + 1) + ". " + tc.getTitle());
        expectedLabel.setText("Expected Result: " + tc.getExpected());
        stepsLabel.setText("Steps: " + tc.getSteps());
        automationRefLabel.setText("Automation Reference: " + tc.getAutoRef());
        businessRefLabel.setText("Business Reference: " + tc.getBusiRef());
        moduleLabel.setText("Module: " + tc.getModule());
        idLabel.setText("ID: " + tc.getId());

        setBackground(index % 2 == 0 ? new JBColor(Gray._245, Gray._60) : new JBColor(Gray._230, Gray._45));
        setBorder(JBUI.Borders.customLine(JBColor.border(), 1, 0, 1, 0));

        expectedLabel.setVisible(activeDetails.contains("Expected Result"));
        stepsLabel.setVisible(activeDetails.contains("Steps"));
        automationRefLabel.setVisible(activeDetails.contains("Automation Ref"));
        businessRefLabel.setVisible(activeDetails.contains("Business Ref"));
        moduleLabel.setVisible(activeDetails.contains("Module"));
        idLabel.setVisible(activeDetails.contains("ID"));

        badgePanel.removeAll();

        // 🌟 2. كود رسم الشارة الحمراء للاختبارات غير المرتبة
        if (isUnsorted) {
            JBLabel unsortedBadge = new JBLabel("Unsorted");
            unsortedBadge.setOpaque(true);
            unsortedBadge.setBackground(new JBColor(new Color(255, 200, 200), new Color(130, 50, 50)));
            unsortedBadge.setForeground(JBColor.RED);
            unsortedBadge.setFont(JBUI.Fonts.smallFont().asBold());
            badgePanel.add(unsortedBadge);
        }

        if (showPriority) badgePanel.add(Shared.createPriorityBadge(tc));
        if (showGroups) {
            List<GroupType> groups = tc.getGroups();
            if (groups != null)
                for (GroupType groupName : groups)
                    badgePanel.add(Shared.createGroupBadge(groupName));
        }
        badgePanel.revalidate();
        badgePanel.repaint();
    }

    // 🌟 4. الدالة التي يناديها الـ TestListRenderer عند مرور الماوس
    public void setHovered(boolean isHovered, String hoveredIconName) {
        if (actionPanel.isVisible() != isHovered) {
            actionPanel.setVisible(isHovered);
        }

        if (isHovered) {
            // 🌟 استخدام لون الـ Hover الرسمي الخاص بـ IntelliJ للأزرار!
            Color hoverColor = JBUI.CurrentTheme.ActionButton.hoverBackground();

            navigateIcon.setBackground("NAVIGATE".equals(hoveredIconName) ? hoverColor : UIUtil.TRANSPARENT_COLOR);
            runIcon.setBackground("RUN".equals(hoveredIconName) ? hoverColor : UIUtil.TRANSPARENT_COLOR);
        } else {
            navigateIcon.setBackground(UIUtil.TRANSPARENT_COLOR);
            runIcon.setBackground(UIUtil.TRANSPARENT_COLOR);
        }
    }

    private JBLabel createDetailLabel() {
        JBLabel label = new JBLabel();
        label.setFont(UIUtil.getLabelFont(UIUtil.FontSize.NORMAL));
        label.setForeground(UIUtil.getContextHelpForeground());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
}
