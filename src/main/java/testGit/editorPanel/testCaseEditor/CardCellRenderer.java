package testGit.editorPanel.testCaseEditor;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.*;

/**
 * Handles the rendering of each TestCase card within the JBList.
 */
public class CardCellRenderer implements ListCellRenderer<TestCase> {
    private final TestCaseCard rendererCard = new TestCaseCard();

    @Override
    public Component getListCellRendererComponent(final JList<? extends TestCase> list, final TestCase tc, final int index, final boolean isSelected, final boolean cellHasFocus) {
        rendererCard.updateData(index, tc);

        // Selection Border
        if (isSelected) {
            rendererCard.setBorder(JBUI.Borders.customLine(JBColor.blue, 1));
        } else {
            rendererCard.setBorder(JBUI.Borders.empty(1));
        }

        return rendererCard;
    }
}