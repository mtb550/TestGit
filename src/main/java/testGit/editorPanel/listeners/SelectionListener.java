package testGit.editorPanel.listeners;


import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBList;
import testGit.editorPanel.testCaseEditor.TestEditorUI;
import testGit.pojo.dto.TestCaseDto;
import testGit.viewPanel.ViewPanel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SelectionListener implements ListSelectionListener {
    private final JBList<TestCaseDto> list;
    private final ToolWindow toolWindow = ViewPanel.getToolWindow();
    private final TestEditorUI ui;

    public SelectionListener(final JBList<TestCaseDto> list, final TestEditorUI ui) {
        this.list = list;
        this.ui = ui;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {

            TestCaseDto selected = list.getSelectedValue();
            if (selected != null) {

                if (toolWindow != null && toolWindow.isVisible()) {
                    ViewPanel.show(selected);
                }

                //com.intellij.openapi.vcs.changes.ChangeListManager.getInstance(Config.getProject()).getAllChanges().forEach(ContentExtractor::printJsonChanges);

                if (ui.getStatusBar() != null) {
                    ui.getStatusBar().updateSelectionState(
                            list.getSelectedIndices(),
                            ui.getCurrentPage(),
                            ui.getPageSize(),
                            ui.getAllTestCaseDtos().size()
                    );
                }


            }

        }
    }
}