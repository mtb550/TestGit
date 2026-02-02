package testGit.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class AddNewTestCaseDialog extends DialogWrapper {
    private final JBTextField titleField = new JBTextField();
    private final JBLabel charCounter = new JBLabel("0 / 100");

    public AddNewTestCaseDialog() {
        super(true);
        setTitle("New Test Case");
        setOKButtonText("Create");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        // Width set to 600px (approx 30% of standard screen)
        panel.setPreferredSize(new Dimension(600, 100));
        panel.setBorder(JBUI.Borders.empty(10)); // Responsive padding

        GridBagConstraints gbc = new GridBagConstraints();

        // 1. Prompt Label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = JBUI.insetsBottom(4);
        JBLabel promptLabel = new JBLabel("Enter title for new test case:");
        panel.add(promptLabel, gbc);

        // 2. Character Counter (Floating Right)
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        // UIUtil.getContextHelpForeground() automatically switches between light/dark gray
        charCounter.setForeground(UIUtil.getContextHelpForeground());
        charCounter.setFont(UIUtil.getLabelFont(UIUtil.FontSize.SMALL));
        panel.add(charCounter, gbc);

        // 3. Modern Text Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        titleField.getEmptyText().setText("Max 100 characters");
        panel.add(titleField, gbc);

        // Live theme-aware counter logic
        titleField.addCaretListener(e -> {
            int len = titleField.getText().length();
            charCounter.setText(len + " / 100");

            if (len > 100) {
                charCounter.setForeground(JBColor.RED); // Error color for both themes
            } else {
                charCounter.setForeground(UIUtil.getContextHelpForeground());
            }
        });

        return panel;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        String text = titleField.getText().trim();
        if (text.isEmpty()) return new ValidationInfo("Title cannot be empty", titleField);
        if (text.length() > 100) return new ValidationInfo("Title is too long", titleField);
        return null;
    }

    public String getInput() {
        return titleField.getText().trim();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return titleField;
    }
}