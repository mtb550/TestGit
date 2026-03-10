package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TestRunOpeningUI implements Disposable {

    private final List<TestCase> testCases;
    private TestRunCard selectedCard = null;

    public TestRunOpeningUI(VirtualFileImpl vf) {
        this.testCases = vf.getTestCases();
    }

    public JComponent createEditorPanel() {
        JPanel cardList = new JPanel();
        cardList.setLayout(new BoxLayout(cardList, BoxLayout.Y_AXIS));
        cardList.setBackground(UIUtil.getTreeBackground());
        cardList.setOpaque(true);

        for (int i = 0; i < testCases.size(); i++) {
            TestCase tc = testCases.get(i);
            TestRunCard card = new TestRunCard(i, tc);
            card.setSelectionListener(this::handleCardSelected);
            cardList.add(card);
        }

        cardList.add(Box.createVerticalGlue());

        JBScrollPane scrollPane = new JBScrollPane(cardList);
        scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);

        JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private void handleCardSelected(TestRunCard newlySelected) {
        if (selectedCard != null && selectedCard != newlySelected) {
            selectedCard.deselect();
        }
        selectedCard = newlySelected;
    }

    @Override
    public void dispose() {
    }
}
