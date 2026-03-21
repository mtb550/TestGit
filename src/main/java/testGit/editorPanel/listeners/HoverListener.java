package testGit.editorPanel.listeners;

import com.intellij.ui.components.JBList;
import testGit.editorPanel.testCaseEditor.TestEditorUI;
import testGit.pojo.dto.TestCaseDto;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

///  implement this to show the run test icon, navigate to code icon.
public class HoverListener extends MouseAdapter {

    private final JBList<TestCaseDto> list;
    private final TestEditorUI ui;

    public HoverListener(JBList<TestCaseDto> list, TestEditorUI ui) {
        this.list = list;
        this.ui = ui;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        // Find which row the mouse is currently over
//        int index = list.locationToIndex(e.getPoint());
//
//        // Only trigger a repaint if the mouse moved to a DIFFERENT row
//        if (index != ui.getHoveredIndex()) {
//            ui.setHoveredIndex(index);
//            list.repaint(); // Force the list to redraw the rubber stamps!
//        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        // When the mouse leaves the list entirely, clear the hover state
//        if (ui.getHoveredIndex() != -1) {
//            ui.setHoveredIndex(-1);
//            list.repaint();
//        }
    }
}