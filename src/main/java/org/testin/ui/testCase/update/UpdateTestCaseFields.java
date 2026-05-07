package org.testin.ui.testCase.update;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.DumbAwareAction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.ui.testCase.ICreateTestCaseSection;
import org.testin.ui.testCase.TestCaseUIBase;
import org.testin.ui.testCase.update.bulk.*;
import org.testin.util.KeyboardSet;
import org.testin.util.autoGenerator.CodeGenerator;
import org.testin.util.statusBar.IStatusBarItem;

import javax.swing.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public enum UpdateTestCaseFields implements IStatusBarItem {
    SAVE(
            "Save",
            KeyboardSet.Enter,
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    ADD_STEP(
            "Add Step",
            KeyboardSet.CreateTestCaseAddStep,
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    REMOVE_STEP(
            "Remove Step",
            KeyboardSet.CreateTestCaseRemoveStep,
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    NAVIGATE_TAB(
            "Navigate",
            KeyboardSet.TabNext.getShortcutText() + " / " + KeyboardSet.TabPrevious.getShortcutText(),
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    NAVIGATE_ARROWS(
            "Navigate Priority",
            KeyboardSet.ArrowUp.getShortcutText() + " / " + KeyboardSet.ArrowDown.getShortcutText(),
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    DESCRIPTION(
            "Title",
            KeyboardSet.UpdateTestCaseDescription,
            AllIcons.Actions.Edit,
            new IStatusBarItem[]{SAVE},
            true,
            1, // todo, change to separate enum and call it here, UpdateTestCaseChangeType.UpdateDescritption
            (items, updatedItems) -> new DescriptionBulkSection().show(items, updatedItems),
            TestCaseUIBase::getDescriptionSection
    ),

    EXPECTED(
            "Expected Results",
            KeyboardSet.UpdateTestCaseExpected,
            AllIcons.General.InspectionsOK,
            new IStatusBarItem[]{SAVE},
            true,
            2, // todo, change to separate enum and call it here, UpdateTestCaseChangeType.UpdateExpectedResult
            (items, updatedItems) -> new ExpectedBulkSection().show(items, updatedItems),
            TestCaseUIBase::getExpectedResultSection
    ),

    AUTO_COMPLETE(
            "Auto Complete",
            KeyboardSet.AutoComplete.getShortcutText(),
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    STEPS(
            "Steps",
            KeyboardSet.UpdateTestCaseSteps,
            AllIcons.Actions.ListFiles,
            new IStatusBarItem[]{SAVE, ADD_STEP, REMOVE_STEP, NAVIGATE_TAB, AUTO_COMPLETE},
            true,
            3, // todo, change to separate enum and call it here, UpdateTestCaseChangeType.UpdateSteps
            (items, updatedItems) -> new StepsBulkSection().show(items, updatedItems),
            TestCaseUIBase::getStepsSection
    ),

    SET_PRIORITY(
            "Set Priority",
            KeyboardSet.PriorityHigh.getShortcutText() + " / " + KeyboardSet.PriorityMedium.getShortcutText() + " / " + KeyboardSet.PriorityLow.getShortcutText(),
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    PRIORITY(
            "Priority",
            KeyboardSet.UpdateTestCasePriority,
            AllIcons.Nodes.Favorite,
            new IStatusBarItem[]{SAVE, NAVIGATE_ARROWS, SET_PRIORITY},
            true,
            4,// todo, change to separate enum and call it here, UpdateTestCaseChangeType.UpdatePriority
            (items, updatedItems) -> new PriorityBulkSection().show(items, updatedItems),
            TestCaseUIBase::getPrioritySection
    ),

    SELECT_GROUP(
            "Select / Unselect Group",
            KeyboardSet.SelectGroup,
            null,
            new IStatusBarItem[]{},
            false,
            0,
            null,
            null
    ),

    GROUP(
            "Group",
            KeyboardSet.UpdateTestCaseGroup,
            AllIcons.Nodes.Tag,
            new IStatusBarItem[]{SAVE, NAVIGATE_TAB, SELECT_GROUP},
            true,
            5, // todo, change to separate enum and call it here, UpdateTestCaseChangeType.UpdateGroup
            (items, updatedItems) -> new GroupBulkSection().show(items, updatedItems),
            TestCaseUIBase::getGroupSection
    );

    private final String name;
    private final KeyboardSet shortcut;
    private final String customShortcutText;
    private final Icon icon;
    private final IStatusBarItem[] statusBarItems;
    private final boolean editMenuItem;
    private final int changeType;
    private final IBulkEditorAction bulkAction;
    private final Function<TestCaseUIBase, ICreateTestCaseSection> sectionExtractor;

    UpdateTestCaseFields(final String name, final KeyboardSet shortcut, final Icon icon, final IStatusBarItem[] statusBarItems, final boolean editMenuItem, final int changeType, final IBulkEditorAction bulkAction, final Function<TestCaseUIBase, ICreateTestCaseSection> sectionExtractor) {
        this.name = name;
        this.shortcut = shortcut;
        this.customShortcutText = null;
        this.icon = icon;
        this.statusBarItems = statusBarItems;
        this.editMenuItem = editMenuItem;
        this.changeType = changeType;
        this.bulkAction = bulkAction;
        this.sectionExtractor = sectionExtractor;
    }

    UpdateTestCaseFields(final String name, final String customShortcutText, final Icon icon, final IStatusBarItem[] statusBarItems, final boolean editMenuItem, final int changeType, final IBulkEditorAction bulkAction, final Function<TestCaseUIBase, ICreateTestCaseSection> sectionExtractor) {
        this.name = name;
        this.shortcut = null;
        this.customShortcutText = customShortcutText;
        this.icon = icon;
        this.statusBarItems = statusBarItems;
        this.editMenuItem = editMenuItem;
        this.changeType = changeType;
        this.bulkAction = bulkAction;
        this.sectionExtractor = sectionExtractor;
    }

    @Override
    public String getShortcutText() {
        if (customShortcutText != null) {
            return customShortcutText;
        }
        return shortcut != null ? shortcut.getShortcutText() : "";
    }

    public void bindShortcut(final JComponent component, final Runnable onTrigger) {
        if (this.shortcut != null) {
            new DumbAwareAction() {
                @Override
                public void actionPerformed(@NotNull com.intellij.openapi.actionSystem.AnActionEvent e) {
                    onTrigger.run();
                }
            }.registerCustomShortcutSet(this.shortcut.getCustomShortcut(), component);
        }
    }

    public interface IBulkEditorAction {
        void show(final List<TestCaseDto> items, final BiConsumer<List<TestCaseDto>, CodeGenerator> updatedItems);
    }
}