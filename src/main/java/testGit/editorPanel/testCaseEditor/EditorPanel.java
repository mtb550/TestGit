package testGit.editorPanel.testCaseEditor;

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
import testGit.pojo.Directory;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.List;

public class EditorPanel extends UserDataHolderBase implements FileEditor {
    private final JBPanel<?> panel;
    private final VirtualFile file;
    private final JBList<TestCase> list; // Keep a reference to the list for focusing

    public EditorPanel(@NotNull List<TestCase> testCases, @NotNull Directory dir, @NotNull VirtualFile file) {
        this.file = file;
        this.panel = new JBPanel<>(new BorderLayout());

        CollectionListModel<TestCase> model = new CollectionListModel<>(testCases);
        this.list = new JBList<>(model);

        // UI Configuration
        list.getEmptyText().setText("No test cases found")
                .appendLine("Press Ctrl+M to add");
        list.setOpaque(true);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setDragEnabled(true);
        list.setDropMode(DropMode.INSERT);

        // Logic & Listeners
        list.setCellRenderer(new CardCellRenderer());
        list.addListSelectionListener(new CardSelectionListener(list));
        list.addMouseListener(new MouseAdapter(list, model, dir));
        list.setTransferHandler(new DragDropHandler(dir, model));
        ShortcutHandler.register(dir, list, model);

        panel.add(new JBScrollPane(list), BorderLayout.CENTER);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return panel;
    }

    /**
     * This is the critical fix for focusing.
     * By returning the list, IntelliJ knows to put the keyboard focus
     * directly on your test cases when the tab opens.
     */
    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return list;
    }

    @Override
    public @NotNull String getName() {
        return "Test Case Cards";
    }

    @Override
    public @NotNull VirtualFile getFile() {
        return file;
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
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener l) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener l) {
    }
}