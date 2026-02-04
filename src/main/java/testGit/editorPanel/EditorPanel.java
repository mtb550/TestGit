package testGit.editorPanel;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.List;

public class EditorPanel extends UserDataHolderBase implements FileEditor {
    private final JBPanel<?> panel;
    private final VirtualFile file;

    public EditorPanel(@NotNull List<TestCase> testCases, @NotNull String featurePath, @NotNull VirtualFile file) {
        this.file = file;
        this.panel = new JBPanel<>(new BorderLayout());

        CollectionListModel<TestCase> model = new CollectionListModel<>(testCases);
        JBList<TestCase> list = new JBList<>(model);

        list.getEmptyText().setText("No test cases found").appendLine("Press Ctrl+M to add");
        list.setOpaque(true);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);

        list.setCellRenderer(new CardCellRenderer());
        list.addListSelectionListener(new CardSelectionListener(list));
        list.addMouseListener(new MouseAdapter(list, model, featurePath));
        list.setTransferHandler(new DragDropHandler(featurePath, model));
        ShortcutHandler.register(featurePath, list, model);

        panel.add(new JBScrollPane(list), BorderLayout.CENTER);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return panel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return panel;
    }

    @Override
    public @NotNull String getName() {
        return "Test Case Cards";
    }

    @Override
    public void dispose() {
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public @NotNull VirtualFile getFile() {
        return file;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener l) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener l) {
    }
}