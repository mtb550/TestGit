package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import lombok.Getter;
import testGit.pojo.TestCase;
import testGit.pojo.TestRun;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Getter
public class TestRunOpeningUI implements Disposable {
    private final List<TestCase> initialTestCases;
    private TestRun currentTestRun;

    public TestRunOpeningUI(List<TestCase> initialTestCases) {
        this.initialTestCases = initialTestCases;
    }

    public JComponent createEditorPanel() {
        // The Root Panel
        JBPanel<?> mainPanel = new JBPanel<>(new BorderLayout());

        // The Container for cards
        JPanel cardList = new JPanel();
        cardList.setLayout(new BoxLayout(cardList, BoxLayout.Y_AXIS));
        cardList.setBackground(UIUtil.getTreeBackground());

        // Add cards
        for (int i = 0; i < initialTestCases.size(); i++) {
            TestCase tc = initialTestCases.get(i);

            // 1. Create the card
            TestRunCard card = new TestRunCard(i, tc);

            // 2. YOU MUST CALL THIS LINE:
            card.updateData(i, tc);

            // 3. Add to the list
            cardList.add(card);
        }

        // Glue at the bottom to keep cards at the top if list is short
        cardList.add(Box.createVerticalGlue());

        JBScrollPane scrollPane = new JBScrollPane(cardList);
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    @Override
    public void dispose() {
    }
}