package testGit.editorPanel.listeners;

import com.intellij.ui.CollectionListModel;
import lombok.Setter;
import testGit.editorPanel.testCaseEditor.TestEditorUI;
import testGit.pojo.dto.TestCaseDto;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class ModelSyncListener implements ListDataListener {
    private final TestEditorUI ui;
    private final CollectionListModel<TestCaseDto> model;
    private boolean active = true;
    @Setter
    private UpdateCallback onUpdateCallback;

    public ModelSyncListener(TestEditorUI ui, CollectionListModel<TestCaseDto> model) {
        this.ui = ui;
        this.model = model;
    }

    public void pause() {
        this.active = false;
    }

    public void resume() {
        this.active = true;
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        if (!active) return;

        int globalStart = (ui.getCurrentPage() - 1) * ui.getPageSize() + e.getIndex0();
        TestCaseDto newlyAdded = null;

        for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
            TestCaseDto item = model.getElementAt(i);
            if (!ui.getAllTestCaseDtos().contains(item)) {
                ui.getAllTestCaseDtos().add(globalStart++, item);
                newlyAdded = item;
            }
        }

        ui.updateSequenceAndSaveAll();

        if (onUpdateCallback != null) {
            SwingUtilities.invokeLater(() -> onUpdateCallback.onUpdate());
        }

        if (newlyAdded != null) {
            final TestCaseDto target = newlyAdded;
            SwingUtilities.invokeLater(() -> ui.selectTestCase(target));
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        if (!active) return;

        int globalStart = (ui.getCurrentPage() - 1) * ui.getPageSize();
        int pageEnd = Math.min(globalStart + ui.getPageSize(), ui.getAllTestCaseDtos().size());

        if (globalStart >= ui.getAllTestCaseDtos().size()) return;

        List<TestCaseDto> pageInMaster = new ArrayList<>(ui.getAllTestCaseDtos().subList(globalStart, pageEnd));
        List<TestCaseDto> pageInModel = model.getItems();

        boolean changed = false;
        for (TestCaseDto tc : pageInMaster) {
            if (!pageInModel.contains(tc)) {
                ui.getAllTestCaseDtos().remove(tc);
                changed = true;
            }
        }

        if (changed) {
            ui.updateSequenceAndSaveAll();
        }

        if (onUpdateCallback != null) {
            SwingUtilities.invokeLater(() -> onUpdateCallback.onUpdate());
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
    }

    public interface UpdateCallback {
        void onUpdate();
    }
}