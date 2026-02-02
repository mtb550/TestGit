package testGit.editorPanel;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import testGit.pojo.TestCase;
import testGit.viewPanel.TestCaseToolWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CardMouseAdapter extends MouseAdapter {
    private final JBList<TestCase> list;
    private final CollectionListModel<TestCase> model;
    private final String featurePath;

    public CardMouseAdapter(JBList<TestCase> list, CollectionListModel<TestCase> model, String featurePath) {
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
        if (!e.isPopupTrigger()) return;

        int idx = list.locationToIndex(e.getPoint());
        if (idx < 0) return;

        // If user right-clicks an item NOT in the current selection, select only that item
        // Otherwise, keep the multi-selection intact for the context menu
        if (!list.isSelectedIndex(idx)) {
            list.setSelectedIndex(idx);
        }

        TestCase tc = model.getElementAt(idx);

        // Create the theme-aware IntelliJ context menu
        ContextMenu contextMenu = new ContextMenu(featurePath, list, model, tc);
        ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu(
                ActionPlaces.TOOLWINDOW_POPUP,
                contextMenu
        );

        popupMenu.getComponent().show(e.getComponent(), e.getX(), e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Handle Double-Click to open details
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            int idx = list.locationToIndex(e.getPoint());
            if (idx >= 0) {
                TestCaseToolWindow.show(model.getElementAt(idx));
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        list.setCursor(Cursor.getDefaultCursor());
    }
}