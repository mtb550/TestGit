package testGit.editorPanel.toolBar;

import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import testGit.pojo.Groups;
import testGit.pojo.Priority;
import testGit.pojo.TestCaseAttributes;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class FilterPopupBuilder {
    public static void showDetailsPopup(final JButton anchor, final Set<String> selectedDetails, final Set<Groups> selectedGroups, final Consumer<Void> onChange) {
        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(JBUI.Borders.empty(10));

        Arrays.stream(TestCaseAttributes.values())
                .filter(TestCaseAttributes::isStandardOption)
                .forEach(option -> mainPanel.add(createCheckboxRow(
                        option.getDisplayName(),
                        null,
                        selectedDetails.contains(option.name()),
                        state -> updateDetails(state, option.name(), selectedDetails, onChange)
                )));

        mainPanel.add(Box.createVerticalStrut(JBUI.scale(5)));
        mainPanel.add(new TitledSeparator(TestCaseAttributes.PRIORITY.getDisplayName()));

        mainPanel.add(createCheckboxRow(
                "Show " + TestCaseAttributes.PRIORITY.getDisplayName() + " Badge",
                null,
                selectedDetails.contains(TestCaseAttributes.PRIORITY.name()),
                state -> updateDetails(state, TestCaseAttributes.PRIORITY.name(), selectedDetails, onChange)
        ));

        Arrays.stream(Priority.values()).forEach(p -> mainPanel.add(createCheckboxRow(
                p.getDisplayName(),
                p.getIcon(),
                selectedDetails.contains(p.name()),
                state -> updateDetails(state, p.name(), selectedDetails, onChange)
        )));

        mainPanel.add(Box.createVerticalStrut(JBUI.scale(5)));
        mainPanel.add(new TitledSeparator(TestCaseAttributes.GROUPS.getDisplayName()));

        mainPanel.add(createCheckboxRow(
                "Show " + TestCaseAttributes.GROUPS.getDisplayName() + " Badge",
                null,
                selectedDetails.contains(TestCaseAttributes.GROUPS.name()),
                state -> updateDetails(state, TestCaseAttributes.GROUPS.name(), selectedDetails, onChange)
        ));

        Arrays.stream(Groups.values()).forEach(g -> mainPanel.add(createCheckboxRow(
                g.getDisplayName(),
                null,
                selectedGroups.contains(g),
                state -> {
                    if (state) selectedGroups.add(g);
                    else selectedGroups.remove(g);
                    Optional.ofNullable(onChange).ifPresent(c -> c.accept(null));
                }
        )));

        final JBScrollPane scrollPane = new JBScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        scrollPane.setPreferredSize(new Dimension(
                mainPanel.getPreferredSize().width + JBUI.scale(40),
                Math.min(mainPanel.getPreferredSize().height, JBUI.scale(500))
        ));

        JBPopupFactory.getInstance()
                .createComponentPopupBuilder(scrollPane, null)
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setResizable(true)
                .createPopup()
                .showUnderneathOf(anchor);
    }

    private static JPanel createCheckboxRow(final String title, final Icon icon, final boolean isSelected, final Consumer<Boolean> onToggle) {
        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, JBUI.scale(2)));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setOpaque(false);

        final JBCheckBox cb = new JBCheckBox(title, isSelected);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.addActionListener(e -> onToggle.accept(cb.isSelected()));

        row.add(cb);

        if (icon != null) {
            row.add(Box.createHorizontalStrut(JBUI.scale(5)));
            row.add(new JBLabel(icon));
        }

        return row;
    }

    private static void updateDetails(final boolean state, final String key, final Set<String> selectedDetails, final Consumer<Void> onChange) {
        if (state) selectedDetails.add(key);
        else selectedDetails.remove(key);
        Optional.ofNullable(onChange).ifPresent(c -> c.accept(null));
    }
}