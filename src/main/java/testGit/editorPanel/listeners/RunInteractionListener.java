package testGit.editorPanel.listeners;

import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
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

    // 🌟 حساب المنطقة في اليمين (Dynamic Height Hitbox)
    private String getActionAtPoint(int index, int xInCell, int yInCell, Rectangle bounds) {
        if (index == -1 || !list.isSelectedIndex(index)) return null;

        int rightPadding = JBUI.scale(16);
        int topBottomPadding = JBUI.scale(12);
        int actionWidth = JBUI.scale(90); // نفس العرض الذي حددناه في RunCard

        // بداية الأزرار من جهة اليمين
        int actionStartX = bounds.width - rightPadding - actionWidth;
        int actionEndX = bounds.width - rightPadding;

        // هل الماوس في منطقة الأزرار (أفقياً)؟
        if (xInCell >= actionStartX && xInCell <= actionEndX) {

            // حساب الارتفاع المتاح للأزرار (الارتفاع الكلي ناقص الهوامش العلوية والسفلية)
            int usableHeight = bounds.height - (topBottomPadding * 2);
            int relativeY = yInCell - topBottomPadding;

            if (relativeY >= 0 && relativeY <= usableHeight) {
                // تقسيم الارتفاع على 3 لمعرفة الزر
                int chunk = usableHeight / 3;

                if (relativeY < chunk) return "PASSED";
                if (relativeY < chunk * 2) return "FAILED";
                return "BLOCKED";
            }
        }
        return null;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int index = list.locationToIndex(e.getPoint());
        if (index == -1) return;

        Rectangle bounds = list.getCellBounds(index, index);
        String currentIcon = getActionAtPoint(index, e.getX() - bounds.x, e.getY() - bounds.y, bounds);

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
        String action = getActionAtPoint(index, e.getX() - bounds.x, e.getY() - bounds.y, bounds);

        if (action != null) {
            System.out.println("Test Case [" + tc.getTitle() + "] updated to: " + action);
            e.consume();
        }
    }
}