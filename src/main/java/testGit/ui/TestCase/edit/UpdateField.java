package testGit.ui.TestCase.edit;

import com.intellij.icons.AllIcons;
import lombok.Getter;
import testGit.util.KeyboardSet;
import testGit.util.statusBar.StatusBarItem;

import javax.swing.*;

@Getter
public enum UpdateField implements StatusBarItem {
    SAVE(
            "Save",
            KeyboardSet.Enter,
            null,
            new StatusBarItem[]{},
            false
    ),

    ADD_STEP(
            "Add Step",
            KeyboardSet.CreateTestCaseAddStep,
            null,
            new StatusBarItem[]{},
            false
    ),

    REMOVE_STEP(
            "Remove Step",
            KeyboardSet.CreateTestCaseRemoveStep,
            null,
            new StatusBarItem[]{},
            false
    ),

    NAVIGATE_TAB(
            "Navigate",
            KeyboardSet.TabNext.getShortcutText() + " / " + KeyboardSet.TabPrevious.getShortcutText(),
            null,
            new StatusBarItem[]{},
            false
    ),

    NAVIGATE_ARROWS(
            "Navigate Priority",
            KeyboardSet.ArrowUp.getShortcutText() + " / " + KeyboardSet.ArrowDown.getShortcutText(),
            null,
            new StatusBarItem[]{},
            false
    ),

    TITLE(
            "Title",
            KeyboardSet.UpdateTestCaseTitle,
            AllIcons.Actions.Edit,
            new StatusBarItem[]{SAVE},
            true
    ),

    EXPECTED(
            "Expected Results",
            KeyboardSet.UpdateTestCaseExpected,
            AllIcons.General.InspectionsOK,
            new StatusBarItem[]{SAVE},
            true
    ),

    AUTO_COMPLETE(
            "Auto Complete",
            KeyboardSet.AutoComplete.getShortcutText(),
            null,
            new StatusBarItem[]{},
            false
    ),

    STEPS(
            "Steps",
            KeyboardSet.UpdateTestCaseSteps,
            AllIcons.Actions.ListFiles,
            new StatusBarItem[]{SAVE, ADD_STEP, REMOVE_STEP, NAVIGATE_TAB, AUTO_COMPLETE},
            true
    ),

    SET_PRIORITY(
            "Set Priority",
            KeyboardSet.PriorityHigh.getShortcutText() + " / " + KeyboardSet.PriorityMedium.getShortcutText() + " / " + KeyboardSet.PriorityLow.getShortcutText(),
            null,
            new StatusBarItem[]{},
            false
    ),

    PRIORITY(
            "Priority",
            KeyboardSet.UpdateTestCasePriority,
            AllIcons.Nodes.Favorite,
            new StatusBarItem[]{SAVE, NAVIGATE_ARROWS, SET_PRIORITY},
            true
    ),

    SELECT_GROUP(
            "Select / Unselect Group",
            KeyboardSet.SelectGroup,
            null,
            new StatusBarItem[]{},
            false
    ),

    GROUPS(
            "Groups",
            KeyboardSet.UpdateTestCaseGroups,
            AllIcons.Nodes.Tag,
            new StatusBarItem[]{SAVE, NAVIGATE_TAB, SELECT_GROUP},
            true
    );

    private final String label;
    private final KeyboardSet shortcut;
    private final String customShortcutText;
    private final Icon icon;
    private final StatusBarItem[] statusBarItems;
    private final boolean editMenuItem;

    UpdateField(final String label, final KeyboardSet shortcut, final Icon icon, final StatusBarItem[] statusBarItems, final boolean editMenuItem) {
        this.label = label;
        this.shortcut = shortcut;
        this.customShortcutText = null;
        this.icon = icon;
        this.statusBarItems = statusBarItems;
        this.editMenuItem = editMenuItem;
    }

    UpdateField(final String label, final String customShortcutText, final Icon icon, final StatusBarItem[] statusBarItems, final boolean editMenuItem) {
        this.label = label;
        this.shortcut = null;
        this.customShortcutText = customShortcutText;
        this.icon = icon;
        this.statusBarItems = statusBarItems;
        this.editMenuItem = editMenuItem;
    }

    @Override
    public String getShortcutText() {
        if (customShortcutText != null) {
            return customShortcutText;
        }
        return shortcut != null ? shortcut.getShortcutText() : "";
    }
}