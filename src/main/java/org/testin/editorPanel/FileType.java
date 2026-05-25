package org.testin.editorPanel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.ex.FakeFileType;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class FileType extends FakeFileType {

    public static final FileType TEST_RUN = new FileType(
            "Test Run",
            "Test Run Editor",
            AllIcons.Nodes.Services
    );

    public static final FileType TEST_CASE = new FileType(
            "Test Case",
            "Test Case Editor",
            AllIcons.FileTypes.Text
    );

    @Getter
    @NotNull
    private final String name;

    @Getter
    @NotNull
    private final String description;

    @Getter
    @NotNull
    private final Icon icon;

    private FileType(final @NotNull String name, final @NotNull String description, final @NotNull Icon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @Override
    public boolean isMyFileType(final @NotNull VirtualFile file) {
        return false;
    }

}