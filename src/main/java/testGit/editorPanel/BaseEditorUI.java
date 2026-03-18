package testGit.editorPanel;

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public interface BaseEditorUI extends Disposable {
    @NotNull JComponent getComponent();

    @Nullable JComponent getPreferredFocusedComponent();
}