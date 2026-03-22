package testGit.editorPanel.listeners;

import com.intellij.ui.CollectionListModel;
import lombok.Setter;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.List;

public class ModelSyncListener<T> implements ListDataListener {
    private final List<T> masterList;
    private final CollectionListModel<T> model;
    private boolean active = true;
    @Setter
    private UpdateCallback onUpdateCallback;

    public ModelSyncListener(final List<T> masterList, final CollectionListModel<T> model) {
        this.masterList = masterList;
        this.model = model;
    }

    public void pause() {
        this.active = false;
    }

    public void resume() {
        this.active = true;
    }

    @Override
    public void intervalAdded(final ListDataEvent e) {
        if (!active) return;
        for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
            T item = model.getElementAt(i);
            if (!masterList.contains(item)) {
                masterList.add(item);
            }
        }

        if (onUpdateCallback != null) {
            SwingUtilities.invokeLater(() -> onUpdateCallback.onUpdate());
        }
    }

    @Override
    public void intervalRemoved(final ListDataEvent e) {
    }

    @Override
    public void contentsChanged(final ListDataEvent e) {
    }

    public interface UpdateCallback {
        void onUpdate();
    }
}