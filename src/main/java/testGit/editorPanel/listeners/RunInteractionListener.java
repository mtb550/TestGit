package testGit.editorPanel.listeners;

import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.UIUtil;
import testGit.editorPanel.testRunEditor.RunEditorUI;
import testGit.pojo.dto.TestCaseDto;
import testGit.viewPanel.ViewPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RunInteractionListener extends MouseAdapter {

    private final JBList<TestCaseDto> list;
    private final RunEditorUI ui;

    public RunInteractionListener(JBList<TestCaseDto> list, RunEditorUI ui) {
        this.list = list;
        this.ui = ui;
    }

    private String getIconAtPoint(int index, int xInCell, int yInCell) {
        // 🌟 نتفاعل فقط مع الصفوف المحددة كما طلبت
        if (index == -1 || !list.isSelectedIndex(index)) return null;
        if (yInCell > 45) return null;

        TestCaseDto tc = list.getModel().getElementAt(index);
        int globalIndex = ((ui.getCurrentPage() - 1) * ui.getPageSize()) + index;
        String titleText = (globalIndex + 1) + ". " + tc.getTitle();

        Font titleFont = JBFont.label().deriveFont(Font.BOLD, UIUtil.getLabelFont().getSize() + 10.0f);
        Font btnFont = JBFont.regular().asBold();

        FontMetrics fmTitle = list.getFontMetrics(titleFont);
        FontMetrics fmBtn = list.getFontMetrics(btnFont);

        int titleWidth = fmTitle.stringWidth(titleText);

        // حساب عرض الأزرار شامل المساحة الداخلية (Padding: 10 يمين + 10 يسار = 20)
        int passedW = fmBtn.stringWidth("PASSED") + 20;
        int failedW = fmBtn.stringWidth("FAILED") + 20;
        int blockedW = fmBtn.stringWidth("BLOCKED") + 20;

        int startX = 16 + titleWidth + 10;

        int passStartX = startX;
        int passEndX = passStartX + passedW;

        int failStartX = passEndX + 8;
        int failEndX = failStartX + failedW;

        int blockStartX = failEndX + 8;
        int blockEndX = blockStartX + blockedW;

        if (xInCell >= passStartX && xInCell <= passEndX) return "PASSED";
        if (xInCell >= failStartX && xInCell <= failEndX) return "FAILED";
        if (xInCell >= blockStartX && xInCell <= blockEndX) return "BLOCKED";

        return null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int index = list.locationToIndex(e.getPoint());
        if (index == -1) return;

        Rectangle bounds = list.getCellBounds(index, index);
        String currentIcon = getIconAtPoint(index, e.getX() - bounds.x, e.getY() - bounds.y);

        list.setCursor(currentIcon != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        if (currentIcon == null ? ui.getHoveredIconAction() != null : !currentIcon.equals(ui.getHoveredIconAction())) {
            ui.setHoveredIconAction(currentIcon);
            list.repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (ui.getHoveredIconAction() != null) {
            ui.setHoveredIconAction(null);
            list.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index = list.locationToIndex(e.getPoint());
        if (index == -1) return;

        TestCaseDto tc = list.getModel().getElementAt(index);

        if (e.getClickCount() == 2) {
            ViewPanel.show(tc);
            return;
        }

        if (!list.isSelectedIndex(index)) return;

        Rectangle bounds = list.getCellBounds(index, index);
        String action = getIconAtPoint(index, e.getX() - bounds.x, e.getY() - bounds.y);

        if (action != null) {
            System.out.println("Test Case [" + tc.getTitle() + "] updated to: " + action);
            e.consume(); // منع فقدان التحديد عند النقر
        }
    }
}