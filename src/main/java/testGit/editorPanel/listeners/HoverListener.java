package testGit.editorPanel.listeners;

import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import testGit.actions.NavigateToCode;
import testGit.actions.RunTestCase;
import testGit.editorPanel.BaseEditorUI;
import testGit.pojo.dto.TestCaseDto;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HoverListener extends MouseAdapter {

    private final JBList<TestCaseDto> list;
    private final BaseEditorUI ui;

    public HoverListener(JBList<TestCaseDto> list, BaseEditorUI ui) {
        this.list = list;
        this.ui = ui;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int index = list.locationToIndex(e.getPoint());
        String currentIcon = null;

        if (index != -1) {
            Rectangle bounds = list.getCellBounds(index, index);
            int xInCell = e.getX() - bounds.x;
            int yInCell = e.getY() - bounds.y;

            if (yInCell <= JBUI.scale(45)) {
                TestCaseDto tc = list.getModel().getElementAt(index);
                int globalIndex = ((ui.getCurrentPage() - 1) * ui.getPageSize()) + index;
                String titleText = String.format("%d. %s", globalIndex + 1, tc.getTitle());

                Font titleFont = JBFont.label().deriveFont(Font.BOLD, UIUtil.getLabelFont().getSize() + 10.0f);
                FontMetrics fm = list.getFontMetrics(titleFont);
                int titleWidth = fm.stringWidth(titleText);

                int startX = JBUI.scale(16) + titleWidth + JBUI.scale(10);

                int navStartX = startX - JBUI.scale(6);
                int navEndX = startX + JBUI.scale(22);

                int runStartX = navEndX;
                int runEndX = runStartX + JBUI.scale(28);

                if (xInCell >= navStartX && xInCell <= navEndX) {
                    currentIcon = "NAVIGATE";
                    list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else if (xInCell > runStartX && xInCell <= runEndX) {
                    currentIcon = "RUN";
                    list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    list.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            } else {
                list.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }

        boolean needsRepaint = false;

        if (index != ui.getHoveredIndex()) {
            ui.setHoveredIndex(index);
            needsRepaint = true;
        }

        if (currentIcon == null ? ui.getHoveredIconAction() != null : !currentIcon.equals(ui.getHoveredIconAction())) {
            ui.setHoveredIconAction(currentIcon);
            needsRepaint = true;
        }

        if (needsRepaint) list.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (ui.getHoveredIndex() != -1 || ui.getHoveredIconAction() != null) {
            ui.setHoveredIndex(-1);
            ui.setHoveredIconAction(null);
            list.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index = list.locationToIndex(e.getPoint());
        if (index == -1) return;

        String action = ui.getHoveredIconAction();

        if (action != null) {
            TestCaseDto tc = list.getModel().getElementAt(index);
            if (action.equals("NAVIGATE")) {
                NavigateToCode.execute(tc);
            } else if (action.equals("RUN")) {
                RunTestCase.execute(tc);
            }
            e.consume();
        }
    }
}