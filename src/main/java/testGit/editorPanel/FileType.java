package testGit.editorPanel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.ex.FakeFileType;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FileType extends FakeFileType {

    public static final FileType TEST_RUN = new FileType(
            "TestRun",
            "Test Run Editor",
            AllIcons.RunConfigurations.TestState.Run
    );

    public static final FileType TEST_CASE = new FileType(
            "TestCase",
            "Test Case Editor",
            AllIcons.Nodes.Test
    );

    private final String name;
    private final String description;
    private final Icon icon;

    private FileType(String name, String description, Icon icon) {
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    @Override
    public boolean isMyFileType(@NotNull VirtualFile file) {
        return false;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @Nullable Icon getIcon() {
        return icon;
    }
}