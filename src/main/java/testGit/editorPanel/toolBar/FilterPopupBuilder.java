package testGit.editorPanel.toolBar;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.CheckBoxList;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Group;
import testGit.pojo.Priority;
import testGit.pojo.TestCaseAttributes;
import testGit.util.IconManager;

import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class FilterPopupBuilder {
    protected static CheckBoxList<TestCaseAttributes> detailsList;
    protected static DefaultActionGroup filterResetBtn;
    protected static DefaultActionGroup filterPriorityMenu;
    protected static DefaultActionGroup filterGroupMenu;

    public static void detailsPopup(final JButton anchor, final Set<String> selectedDetails, final Consumer<Void> onChange) {
        detailsList = new CheckBoxList<>();
        Arrays.stream(TestCaseAttributes.values())
                .filter(TestCaseAttributes::isStandardToolBarOption)
                .forEach(attr -> detailsList.addItem(attr, attr.getName(), selectedDetails.contains(attr.name())));

        detailsList.setCheckBoxListListener((index, state) -> {
            TestCaseAttributes item = detailsList.getItemAt(index);
            if (item != null) {
                if (state) selectedDetails.add(item.name());
                else selectedDetails.remove(item.name());
            }
            Optional.ofNullable(onChange).ifPresent(c -> c.accept(null));
        });

        // popup
        JBPopupFactory.getInstance()
                .createComponentPopupBuilder(detailsList, detailsList)
                .setRequestFocus(true)
                .createPopup()
                .showUnderneathOf(anchor);
    }

    public static void filterPopup(final JButton anchor, final Set<Priority> selectedPriorities, final Set<Group> selectedGroups, final Runnable onReset, final Consumer<Void> onChange) {
        // reset btn
        filterResetBtn = new DefaultActionGroup();
        filterResetBtn.add(new DumbAwareAction("Reset Filters", "Clear active filters", AllIcons.Actions.Cancel) {
            @Override
            public void update(@NotNull AnActionEvent e) {
                boolean hasActiveFilters = !selectedPriorities.isEmpty() || !selectedGroups.isEmpty();
                e.getPresentation().setEnabledAndVisible(hasActiveFilters);
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }

            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                Optional.ofNullable(onReset).ifPresent(Runnable::run);
            }
        });
        filterResetBtn.addSeparator();

        // priority menu
        filterPriorityMenu = new DefaultActionGroup(TestCaseAttributes.PRIORITY.getName(), true);
        Arrays.stream(Priority.values()).forEach(p ->
                filterPriorityMenu.add(new DumbAwareToggleAction(p.getName(), null, IconManager.createIcon(p.getColor())) {
                    @Override
                    public boolean isSelected(@NotNull AnActionEvent e) {
                        return selectedPriorities.contains(p);
                    }

                    @Override
                    public void setSelected(@NotNull AnActionEvent e, boolean state) {
                        p.onChange(selectedPriorities, state);
                        Optional.ofNullable(onChange).ifPresent(c -> c.accept(null));
                    }

                    @Override
                    public @NotNull ActionUpdateThread getActionUpdateThread() {
                        return ActionUpdateThread.BGT;
                    }
                }));
        filterResetBtn.add(filterPriorityMenu);

        // group menu
        filterGroupMenu = new DefaultActionGroup(TestCaseAttributes.GROUP.getName(), true);
        Arrays.stream(Group.values()).forEach(g ->
                filterGroupMenu.add(new DumbAwareToggleAction(g.getName()) {
                    @Override
                    public boolean isSelected(@NotNull AnActionEvent e) {
                        return selectedGroups.contains(g);
                    }

                    @Override
                    public void setSelected(@NotNull AnActionEvent e, boolean state) {
                        g.onChange(selectedGroups, state);
                        Optional.ofNullable(onChange).ifPresent(c -> c.accept(null));
                    }

                    @Override
                    public @NotNull ActionUpdateThread getActionUpdateThread() {
                        return ActionUpdateThread.BGT;
                    }
                })
        );
        filterResetBtn.add(filterGroupMenu);

        // popup
        JBPopupFactory.getInstance()
                .createActionGroupPopup(null, filterResetBtn,
                        DataManager.getInstance().getDataContext(anchor),
                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                        true)
                .showUnderneathOf(anchor);
    }
}