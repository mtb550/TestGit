package org.testin.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.testin.editorPanel.UnifiedVirtualFile;
import org.testin.pojo.*;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.pojo.dto.TestRunDto;
import org.testin.pojo.dto.dirs.DirectoryDto;
import org.testin.pojo.dto.dirs.TestProjectDirectoryDto;
import org.testin.pojo.dto.dirs.TestRunDirectoryDto;
import org.testin.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class CreateTestRun implements NodeCreator {

    @Override
    public void execute(final CreateTestNode action, final Project project, final String name, final DefaultMutableTreeNode parentNode, final DirectoryDto parentDir, final Path newDirPath) {

        TestRunDto metadata = new TestRunDto();
        metadata.setStatus(TestRunStatus.CREATED);

        TestRunDirectoryDto tr = TestRunDirectoryDto
                .builder()
                .name(name)
                .path(newDirPath)
                .build();

        TreeUtilImpl.createVf(this, parentDir.getPath(), name);
        TreeUtilImpl.createDataVf(this, newDirPath, DirectoryType.TR.getMarker());

        TestProjectDirectoryDto tp = action.getProjectPanel().getTestProjectSelector().getSelectedTestProject().getItem();

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            final Path testCasesPath = tp.getTestCasesDirectory().getPath();

            final DefaultTreeModel fullModel = new DefaultTreeModel(buildDirectoryTree(testCasesPath, true, parentDir));

            final UnifiedVirtualFile virtualFile = new UnifiedVirtualFile(
                    tr,
                    fullModel,
                    new ArrayList<>(),
                    EditorType.TEST_RUN_CREATION,
                    action.getProjectPanel()
            );
            virtualFile.setMetadata(metadata);

            ApplicationManager.getApplication().invokeLater(() ->
                    Optional.ofNullable(FileEditorManager.getInstance(Config.getProject()))
                            .ifPresent(manager -> manager.openFile(virtualFile, true))
            );
        });
    }

    private DefaultMutableTreeNode buildDirectoryTree(final Path folder, final boolean isRoot, DirectoryDto parentDir) {
        final Object label = isRoot
                ? "Test Cases (" + folder.getParent().getFileName().toString() + ")"
                : resolveDirectoryObject(folder, parentDir);

        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(label);

        if (!Files.exists(folder) || !Files.isDirectory(folder)) return node;

        try (final Stream<Path> paths = Files.list(folder)) {

            paths.sorted(Comparator
                            .comparing((Path p) -> !Files.isDirectory(p))
                            .thenComparing(p -> p.getFileName().toString().toLowerCase()))
                    .forEach(child -> {
                        if (Files.isDirectory(child)) {
                            node.add(buildDirectoryTree(child, false, parentDir));
                        } else if (child.toString().endsWith(".json")) {
                            try {
                                final TestCaseDto tc = Config.getMapper().readValue(child.toFile(), TestCaseDto.class);
                                node.add(new DefaultMutableTreeNode(tc));
                            } catch (final Exception e) {
                                System.err.println("Failed to parse test case: " + child.getFileName());
                            }
                        }
                    });
        } catch (final IOException e) {
            System.err.println("Failed to read directory tree: " + folder);
            e.printStackTrace(System.err);
        }

        return node;
    }

    private Object resolveDirectoryObject(final Path folder, DirectoryDto parentDir) {
        if (Files.exists(folder.resolve(DirectoryType.TSP.getMarker())))
            return DirectoryMapper.testSetPackageNode(folder, parentDir);

        if (Files.exists(folder.resolve(DirectoryType.TS.getMarker())))
            return DirectoryMapper.testSetNode(folder, parentDir);

        return folder.getFileName().toString();
    }
}