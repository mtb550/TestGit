package testGit.editorPanel;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import testGit.actions.editorPanel.AddTestCaseAction;
import testGit.pojo.TestCase;

import java.awt.event.MouseEvent;

public class MouseAdapter extends java.awt.event.MouseAdapter {
    private final JBList<TestCase> list;
    private final CollectionListModel<TestCase> model;
    private final String featurePath;

    public MouseAdapter(JBList<TestCase> list, CollectionListModel<TestCase> model, String featurePath) {
        this.list = list;
        this.model = model;
        this.featurePath = featurePath;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        // e.isPopupTrigger() ensures the menu only shows once on the correct right-click event for the OS
        if (!e.isPopupTrigger()) return;

        int idx = list.locationToIndex(e.getPoint());

        // Determine if the click was specifically on a card or the empty background
        boolean isItemClick = idx >= 0 && list.getCellBounds(idx, idx).contains(e.getPoint());


        if (isItemClick) {
            // Logic for right-clicking an existing test case
            if (!list.isSelectedIndex(idx)) {
                list.setSelectedIndex(idx);
            }

            // Add item-specific actions
            ContextMenu ctx = new ContextMenu(featurePath, list, model, model.getElementAt(idx));
            // Add other actions like Edit or Duplicate here
            ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu(
                    ActionPlaces.TOOLWINDOW_POPUP,
                    ctx
            );

            popupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
        } else {
            // Logic for right-clicking EMPTY SPACE
            list.clearSelection();
            DefaultActionGroup ctx = new DefaultActionGroup();
            ctx.add(new AddTestCaseAction(featurePath, list, model));
            ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu(
                    ActionPlaces.TOOLWINDOW_POPUP,
                    ctx
            );

            popupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
        }


    }

    // ... mouseClicked for double-click remains the same ...
}