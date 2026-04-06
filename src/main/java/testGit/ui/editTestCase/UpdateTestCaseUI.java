package testGit.ui.editTestCase;

import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import testGit.pojo.dto.TestCaseDto;
import testGit.ui.createTestCase.*;
import testGit.ui.editTestCase.single.SingleEditorSaveManager;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.function.Consumer;

public class UpdateTestCaseUI extends CreateTestCaseBase {

    public void showForEdit(final TestCaseDto existingDto, final UpdateField targetField, final Consumer<TestCaseDto> onUpdate, final Set<String> uniqueStepsCache) {
        final JBPopup[] popupWrapper = new JBPopup[1];
        UIAction repackPopup = () -> {
            if (popupWrapper[0] != null) popupWrapper[0].pack(false, true);
        };

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(JBUI.Borders.empty(12));

        for (CreateTestCaseSection section : getAllSections()) {
            JPanel slot = new JPanel(new BorderLayout());
            slot.setOpaque(false);

            fillSectionData(section, existingDto, uniqueStepsCache, repackPopup);

            boolean isTarget = isTargetSection(section, targetField);
            section.setEditable(isTarget);

            if (section instanceof TitleSection || isTarget) {
                section.showSection(slot);
                contentPanel.add(slot);
            }

            if (isTarget) {
                section.setupShortcut(mainPanel, slot, this, repackPopup, uniqueStepsCache);
            }
        }

        setupUI(mainPanel, contentPanel, popupWrapper, existingDto, targetField, onUpdate);
    }

    private boolean isTargetSection(final CreateTestCaseSection section, final UpdateField target) {
        if (target == UpdateField.TITLE) return section instanceof TitleSection;
        if (target == UpdateField.EXPECTED) return section instanceof ExpectedSection;
        if (target == UpdateField.PRIORITY) return section instanceof PrioritySection;
        if (target == UpdateField.GROUPS) return section instanceof GroupsSection;
        if (target == UpdateField.STEPS) return section instanceof StepsSection;
        return false;
    }

    private void fillSectionData(final CreateTestCaseSection section, final TestCaseDto dto, final Set<String> cache, final UIAction repack) {
        if (section instanceof TitleSection s) s.getTitleField().setText(dto.getTitle());
        if (section instanceof ExpectedSection s) s.getExpectedField().setText(dto.getExpected());
        if (section instanceof PrioritySection s) s.getCombo().setSelectedItem(dto.getPriority());
        if (section instanceof GroupsSection s) s.setSelectedGroups(dto.getGroups());
        if (section instanceof StepsSection s) s.setStepsData(dto.getSteps(), repack, cache);
    }

    private JComponent getTargetFocus(final UpdateField target) {
        return getAllSections().stream()
                .filter(section -> isTargetSection(section, target))
                .map(CreateTestCaseSection::getFocusComponent)
                .findFirst()
                .orElse(titleSection.getFocusComponent());
    }

    private void setupUI(final JPanel mainPanel, final JPanel contentPanel, final JBPopup[] popupWrapper, final TestCaseDto dto, final UpdateField target, final Consumer<TestCaseDto> onUpdate) {
        JPanel anchorPanel = new JPanel(new BorderLayout());
        anchorPanel.setOpaque(false);
        anchorPanel.add(contentPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JBScrollPane(anchorPanel);
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(statusBar.getPanel(), BorderLayout.SOUTH);

        popupWrapper[0] = JBPopupFactory.getInstance()
                .createComponentPopupBuilder(mainPanel, getTargetFocus(target))
                .setTitle("Edit " + target.getLabel())
                .setRequestFocus(true)
                .setCancelOnClickOutside(true)
                .setMovable(true)
                .setResizable(true)
                .createPopup();

        Runnable saveAction = SingleEditorSaveManager.createSaveAction(this, dto, onUpdate, popupWrapper);

        registerShortcut(mainPanel, testGit.util.KeyboardSet.Enter.getShortcut(), saveAction::run);

        popupWrapper[0].showCenteredInCurrentWindow(testGit.pojo.Config.getProject());
    }


}