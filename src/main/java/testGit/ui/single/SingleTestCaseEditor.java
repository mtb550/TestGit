package testGit.ui.single;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.fields.ExtendableTextComponent;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Config;
import testGit.pojo.Groups;
import testGit.pojo.Priority;
import testGit.pojo.dto.TestCaseDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SingleTestCaseEditor {

    // 🟢 الاعتماد على حجم الخط الافتراضي للمستخدم في IntelliJ
    private static final float BASE_FONT_SIZE = JBUI.Fonts.label().getSize2D();
    private static final float TITLE_FONT_SIZE = BASE_FONT_SIZE + 6f; // للعنوان (أكبر)
    private static final float FIELD_FONT_SIZE = BASE_FONT_SIZE + 2f; // لباقي الحقول

    public static void show(TestCaseDto existingDto, Consumer<TestCaseDto> onSave) {
        boolean isNew = (existingDto == null);
        TestCaseDto dto = isNew ? new TestCaseDto() : existingDto;

        final JBPopup[] popupWrapper = new JBPopup[1];
        Runnable repackPopup = () -> {
            if (popupWrapper[0] != null) popupWrapper[0].pack(true, true);
        };

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(JBUI.Borders.empty());

        mainPanel.setFocusCycleRoot(true);
        mainPanel.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(JBUI.Borders.empty(12));

        // ================== 1. حقل العنوان ==================
        ExtendableTextField titleField = createTextField("Test Case Title", AllIcons.Actions.Edit, TITLE_FONT_SIZE);
        if (!isNew && dto.getTitle() != null) titleField.setText(dto.getTitle());
        contentPanel.add(titleField);

        // ================== 2. الحقول الديناميكية ==================
        ExtendableTextField expectedField = createTextField("Expected Result", AllIcons.General.InspectionsOK, FIELD_FONT_SIZE);
        JPanel expectedWrapper = wrapComponent(expectedField);

        ComboBox<Priority> priorityCombo = createPriorityCombo(FIELD_FONT_SIZE);
        priorityCombo.setSelectedItem(dto.getPriority() != null ? dto.getPriority() : Priority.LOW);
        JPanel priorityWrapper = wrapComponent(priorityCombo);

        JPanel groupsPanel = createGroupsPanel(FIELD_FONT_SIZE);
        JPanel groupsWrapper = wrapComponent(groupsPanel);

        JPanel stepsContainer = new JPanel();
        stepsContainer.setLayout(new BoxLayout(stepsContainer, BoxLayout.Y_AXIS));
        stepsContainer.setOpaque(false);
        JPanel stepsWrapper = wrapComponent(stepsContainer);

        List<ExtendableTextField> stepFields = new ArrayList<>();

        // ================== 3. تعبئة البيانات ==================
        if (!isNew) {
            if (dto.getExpected() != null && !dto.getExpected().isEmpty()) {
                expectedField.setText(dto.getExpected());
                contentPanel.add(expectedWrapper);
            }
            if (dto.getPriority() != null) {
                priorityCombo.setSelectedItem(dto.getPriority());
                contentPanel.add(priorityWrapper);
            }
            if (dto.getGroups() != null && !dto.getGroups().isEmpty()) {
                contentPanel.add(groupsWrapper);
                for (Component c : groupsPanel.getComponents()) {
                    if (c instanceof JBCheckBox checkBox) {
                        if (dto.getGroups().contains(Groups.valueOf(checkBox.getText()))) {
                            checkBox.setSelected(true);
                        }
                    }
                }
            }
            if (dto.getSteps() != null && !dto.getSteps().isEmpty()) {
                contentPanel.add(stepsWrapper);
                for (String step : dto.getSteps()) {
                    addStepField(stepsContainer, stepFields, step, repackPopup);
                }
            }
        }

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // ================== 4. شريط الحالة ==================
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(JBUI.Borders.empty(6, 10));
        statusBar.setBackground(JBUI.CurrentTheme.Popup.headerBackground(false));
        JLabel shortcutLabel = new JLabel("Expected:CTRL+E Steps:CTRL+S Priority:CTRL+P Groups:CTRL+G Save:Enter");
        shortcutLabel.setForeground(JBColor.GRAY);
        shortcutLabel.setFont(JBUI.Fonts.smallFont());
        statusBar.add(shortcutLabel, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        // ================== 5. إنشاء النافذة ==================
        popupWrapper[0] = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(mainPanel, titleField)
                .setTitle(isNew ? "Create Test Case" : "Edit Test Case")
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setMovable(true)
                .setResizable(true)
                .createPopup();

        // ================== 6. تفاعلات التوسعة ==================
        Runnable showExpected = () -> {
            if (expectedWrapper.getParent() == null) contentPanel.add(expectedWrapper);
            repackPopup.run();
            expectedField.requestFocus();
        };

        Runnable showPriority = () -> {
            if (priorityWrapper.getParent() == null) contentPanel.add(priorityWrapper);
            repackPopup.run();
            priorityCombo.requestFocus();
        };

        Runnable showGroups = () -> {
            if (groupsWrapper.getParent() == null) contentPanel.add(groupsWrapper);
            repackPopup.run();
        };

        Runnable showSteps = () -> {
            if (stepsWrapper.getParent() == null) {
                contentPanel.add(stepsWrapper);
            }
            addStepField(stepsContainer, stepFields, "", repackPopup);
            repackPopup.run();
            stepFields.getLast().requestFocus();
        };

        // ================== 7. تسجيل الاختصارات ==================
        registerShortcut(mainPanel, KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK, showExpected);
        registerShortcut(mainPanel, KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK, showPriority);
        registerShortcut(mainPanel, KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK, showGroups);
        registerShortcut(mainPanel, KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, showSteps);
        registerShortcut(mainPanel, KeyEvent.VK_TAB, 0, () -> KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent());
        registerShortcut(mainPanel, KeyEvent.VK_TAB, KeyEvent.SHIFT_DOWN_MASK, () -> KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent());

        // ================== 8. الحفظ ==================
        Runnable saveAction = () -> {
            dto.setTitle(titleField.getText().trim());

            if (expectedWrapper.getParent() != null) dto.setExpected(expectedField.getText().trim());

            if (priorityWrapper.getParent() != null) {
                dto.setPriority((Priority) priorityCombo.getSelectedItem());
            } else if (dto.getPriority() == null) {
                dto.setPriority(Priority.LOW);
            }

            if (groupsWrapper.getParent() != null) {
                List<Groups> selectedGroups = new ArrayList<>();
                for (Component c : groupsPanel.getComponents()) {
                    if (c instanceof JBCheckBox checkBox && checkBox.isSelected()) {
                        selectedGroups.add(Groups.valueOf(checkBox.getText()));
                    }
                }
                dto.setGroups(selectedGroups.isEmpty() ? null : selectedGroups);
            }

            if (stepsWrapper.getParent() != null) {
                List<String> finalSteps = new ArrayList<>();
                for (ExtendableTextField sf : stepFields) {
                    if (!sf.getText().trim().isEmpty()) finalSteps.add(sf.getText().trim());
                }
                dto.setSteps(finalSteps.isEmpty() ? null : finalSteps);
            }

            if (!dto.getTitle().isEmpty()) {
                onSave.accept(dto);
                popupWrapper[0].closeOk(null);
            }
        };
        registerShortcut(mainPanel, KeyEvent.VK_ENTER, 0, saveAction);

        popupWrapper[0].showCenteredInCurrentWindow(Config.getProject());
    }

    // ==========================================
    // UI Helpers
    // ==========================================

    private static ExtendableTextField createTextField(String placeholder, Icon icon, float fontSize) {
        ExtendableTextField textField = new ExtendableTextField();

        // 🟢 ضبط خط الـ TextField بناءً على متغير الـ fontSize
        Font fieldFont = JBFont.regular().deriveFont(fontSize);
        textField.setFont(fieldFont);

        // 🟢 توحيد خط الـ EmptyText (Placeholder) ليكون مطابقاً تماماً لخط النص
        textField.getEmptyText().setFont(fieldFont);
        textField.getEmptyText().setText(placeholder);

        textField.putClientProperty("JTextField.Search.noBorderRing", Boolean.TRUE);
        textField.setBorder(JBUI.Borders.empty(10));

        textField.setExtensions(new ExtendableTextComponent.Extension() {
            @Override
            public Icon getIcon(boolean hovered) {
                return icon;
            }

            @Override
            public boolean isIconBeforeText() {
                return true;
            }

            @Override
            public int getIconGap() {
                return JBUI.scale(8);
            }
        });
        return textField;
    }

    private static void addStepField(JPanel container, List<ExtendableTextField> stepFields, String text, Runnable repackAction) {
        ExtendableTextField stepField = createTextField("Step " + (stepFields.size() + 1), AllIcons.Actions.ListFiles, FIELD_FONT_SIZE);
        stepField.setText(text);

        stepField.addExtension(new ExtendableTextComponent.Extension() {
            @Override
            public Icon getIcon(boolean hovered) {
                return hovered ? AllIcons.Actions.Cancel : AllIcons.General.Remove;
            }

            @Override
            public boolean isIconBeforeText() {
                return false;
            }

            @Override
            public String getTooltip() {
                return "Remove step";
            }

            @Override
            public Runnable getActionOnClick() {
                return () -> {
                    container.remove(stepField);
                    stepFields.remove(stepField);

                    for (int i = 0; i < stepFields.size(); i++) {
                        stepFields.get(i).getEmptyText().setText("Step " + (i + 1));
                    }

                    container.revalidate();
                    container.repaint();
                    repackAction.run();
                };
            }
        });

        stepFields.add(stepField);
        container.add(stepField);
        container.add(Box.createVerticalStrut(JBUI.scale(4)));
    }

    private static JPanel wrapComponent(JComponent component) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(component, BorderLayout.CENTER);
        wrapper.setBorder(JBUI.Borders.emptyTop(8));
        return wrapper;
    }

    private static ComboBox<Priority> createPriorityCombo(float fontSize) {
        ComboBox<Priority> combo = new ComboBox<>(Priority.values());
        // 🟢 ضبط خط الكومبو بوكس
        combo.setFont(JBFont.regular().deriveFont(fontSize));
        combo.setRenderer(new ColoredListCellRenderer<Priority>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends Priority> list, Priority value, int index, boolean selected, boolean hasFocus) {
                if (value != null) {
                    setIcon(value.getIcon());
                    append(" Priority: " + value.name());
                }
            }
        });
        return combo;
    }

    private static JPanel createGroupsPanel(float fontSize) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, JBUI.scale(4), JBUI.scale(4)));
        panel.setOpaque(false);
        for (Groups group : Groups.values()) {
            JBCheckBox checkBox = new JBCheckBox(group.name());
            // 🟢 ضبط خط الشيك بوكس
            checkBox.setFont(JBFont.regular().deriveFont(fontSize - 1f)); // أصغر بقليل ليبدو متناسقاً
            panel.add(checkBox);
        }
        return panel;
    }

    private static void registerShortcut(JComponent component, int keyCode, int modifiers, Runnable action) {
        new DumbAwareAction() {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                action.run();
            }
        }.registerCustomShortcutSet(new CustomShortcutSet(KeyStroke.getKeyStroke(keyCode, modifiers)), component);
    }
}