package testGit.ui.TestCase;

import com.intellij.icons.AllIcons;
import lombok.Getter;
import testGit.util.KeyboardSet;
import testGit.util.statusBar.StatusBarItem;

import javax.swing.*;

@Getter
public enum CreateField implements StatusBarItem {
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

    AUTO_COMPLETE(
            "Auto Complete",
            KeyboardSet.AutoComplete.getShortcutText(),
            null,
            new StatusBarItem[]{},
            false
    ),

    SET_PRIORITY(
            "Set Priority",
            KeyboardSet.PriorityHigh.getShortcutText() + " / " + KeyboardSet.PriorityMedium.getShortcutText() + " / " + KeyboardSet.PriorityLow.getShortcutText(),
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
            KeyboardSet.CreateTestCaseTitle,
            AllIcons.Actions.Edit,
            new StatusBarItem[]{SAVE, NAVIGATE_TAB},
            true
    ),

    EXPECTED(
            "Expected Results",
            KeyboardSet.CreateTestCaseExpected,
            AllIcons.General.InspectionsOK,
            new StatusBarItem[]{SAVE, NAVIGATE_TAB},
            true
    ),

    STEPS(
            "Steps",
            KeyboardSet.CreateTestCaseAddStep,
            AllIcons.Actions.ListFiles,
            new StatusBarItem[]{SAVE, ADD_STEP, REMOVE_STEP, AUTO_COMPLETE, NAVIGATE_TAB},
            true
    ),

    PRIORITY(
            "Priority",
            KeyboardSet.CreateTestCasePriority,
            AllIcons.Nodes.Favorite,
            new StatusBarItem[]{SAVE, SET_PRIORITY, NAVIGATE_ARROWS},
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
            KeyboardSet.CreateTestCaseGroups,
            AllIcons.Nodes.Tag,
            new StatusBarItem[]{SAVE, NAVIGATE_TAB, SELECT_GROUP},
            true
    );

    private final String label;
    private final KeyboardSet shortcut;
    private final String customShortcutText;
    private final Icon icon;
    private final StatusBarItem[] statusBarItems;
    private final boolean createMenuItem;

    CreateField(final String label, final KeyboardSet shortcut, final Icon icon, final StatusBarItem[] statusBarItems, final boolean createMenuItem) {
        this.label = label;
        this.shortcut = shortcut;
        this.customShortcutText = null;
        this.icon = icon;
        this.statusBarItems = statusBarItems;
        this.createMenuItem = createMenuItem;
    }

    CreateField(final String label, final String customShortcutText, final Icon icon, final StatusBarItem[] statusBarItems, final boolean createMenuItem) {
        this.label = label;
        this.shortcut = null;
        this.customShortcutText = customShortcutText;
        this.icon = icon;
        this.statusBarItems = statusBarItems;
        this.createMenuItem = createMenuItem;
    }

    @Override
    public String getShortcutText() {
        if (customShortcutText != null) {
            return customShortcutText;
        }
        return shortcut != null ? shortcut.getShortcutText() : "";
    }
}