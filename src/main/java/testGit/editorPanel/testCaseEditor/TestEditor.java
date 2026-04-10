package testGit.editorPanel.testCaseEditor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import testGit.editorPanel.UnifiedVirtualFile;
import testGit.pojo.Config;
import testGit.pojo.dto.TestCaseDto;
import testGit.pojo.dto.dirs.TestSetDirectoryDto;
import testGit.util.notifications.Notifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestEditor {

    public static void open(final TestSetDirectoryDto ts) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            VirtualFile newVirtualFile = createVirtualFile(ts);

            ApplicationManager.getApplication().invokeLater(() -> {
                FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());
                editorManager.openFile(newVirtualFile, true);
            });
        });
    }

    private static VirtualFile createVirtualFile(final TestSetDirectoryDto testSetDirectory) {
        Path dirPath = testSetDirectory.getPath();
        List<TestCaseDto> testCaseDtos = new ArrayList<>();

        if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
            try (Stream<Path> paths = Files.list(dirPath)) {
                testCaseDtos = paths
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".json"))
                        .parallel()
                        .map(TestEditor::addTestCase)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                System.err.println("Failed to read directory: " + e.getMessage());
            }
        }

        return new UnifiedVirtualFile(testSetDirectory, testCaseDtos);
    }

    private static TestCaseDto addTestCase(final Path filePath) {
        try {
            return Config.getMapper().readValue(filePath.toFile(), TestCaseDto.class);
        } catch (Exception e) {
            Notifier.error("Read Test Case failed", filePath.getFileName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }
}