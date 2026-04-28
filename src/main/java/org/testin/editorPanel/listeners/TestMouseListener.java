package org.testin.editorPanel.listeners;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import org.testin.actions.CreateTestCase;
import org.testin.editorPanel.EditorCM;
import org.testin.editorPanel.IEditorUI;
import org.testin.editorPanel.testRunEditor.RunEditorUI;
import org.testin.pojo.Config;
import org.testin.pojo.TestStatus;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.pojo.dto.dirs.DirectoryDto;
import org.testin.viewPanel.ViewToolWindowFactory;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

// todo, it is used for test and run but name is TestMouseListener, isolate the logic of run in separate class.
public class TestMouseListener extends MouseAdapter {
    private final JBList<TestCaseDto> list;
    private final CollectionListModel<TestCaseDto> model;
    private final EditorCM editorCM;
    private final DefaultActionGroup emptyMenu;
    private final Path path;
    private final IEditorUI ui;

    public TestMouseListener(final IEditorUI ui, final JBList<TestCaseDto> list, final CollectionListModel<TestCaseDto> model, final DirectoryDto dir, final EditorCM editorCM) {
        this.ui = ui;
        this.list = list;
        this.path = dir.getPath();
        this.model = model;
        this.editorCM = editorCM;
        this.emptyMenu = new DefaultActionGroup();
        this.emptyMenu.add(new CreateTestCase(ui, dir.getPath(), list, model));
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final int index = list.locationToIndex(e.getPoint());
        final boolean isClickOnItem = index >= 0 && list.getCellBounds(index, index).contains(e.getPoint());

        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
            if (isClickOnItem) {
                Optional.ofNullable(model.getElementAt(index)).ifPresent(selected -> ViewToolWindowFactory.showPanel(Config.getProject(), List.of(selected), path));
            }
            return;
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            final ActionManager actionManager = ActionManager.getInstance();
            final String place = ActionPlaces.TOOLWINDOW_POPUP;

            if (isClickOnItem) {
                if (!list.isSelectedIndex(index)) {
                    list.setSelectedIndex(index);
                }
                actionManager.createActionPopupMenu(place, editorCM).getComponent().show(e.getComponent(), e.getX(), e.getY());
            } else {
                list.clearSelection();
                actionManager.createActionPopupMenu(place, emptyMenu).getComponent().show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public void mousePressed(MouseEvent e) {

        if (ui instanceof RunEditorUI runUI) {
            String hoveredAction = runUI.getHoveredIconAction();
            if (hoveredAction != null) {
                try {
                    TestStatus status = TestStatus.valueOf(hoveredAction);
                    runUI.updateStatusAndNext(status);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }
}