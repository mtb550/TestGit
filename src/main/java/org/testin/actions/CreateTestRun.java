package org.testin.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.CheckedTreeNode;
import org.testin.pojo.*;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.pojo.dto.TestRunDto;
import org.testin.pojo.dto.dirs.DirectoryDto;
import org.testin.pojo.dto.dirs.TestProjectDirectoryDto;
import org.testin.pojo.dto.dirs.TestRunDirectoryDto;
import org.testin.projectPanel.ProjectPanel;
import org.testin.ui.RunCreationForm;
import org.testin.util.EditorUtil;
import org.testin.util.notifications.Notifier;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class CreateTestRun implements NodeCreator {
    private TestRunDirectoryDto tr;

    @Override
    public DirectoryDto execute(final CreateTestNode action, final Project project, final String name, final DefaultMutableTreeNode parentNode, final DirectoryDto parentDir, final Path newDirPath) {
        final TestProjectDirectoryDto tp = action.getProjectPanel().getTestProjectSelector().getSelectedTestProject().getItem();

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            final Path testCasesPath = tp.getTestCasesDirectory().getPath();
            final DefaultMutableTreeNode fullModelNode = buildDirectoryTree(testCasesPath, true, parentDir);
            final CheckedTreeNode root = convertToCheckedNodes(fullModelNode);

            ApplicationManager.getApplication().invokeLater(() -> {

                final RunCreationForm form = new RunCreationForm(name, root, Collections.emptyMap());

                DialogBuilder dialogBuilder = new DialogBuilder(project);
                dialogBuilder.setTitle("Create Test Run");
                dialogBuilder.setCenterPanel(form.getMainPanel());
                dialogBuilder.addOkAction().setText("Save Test Run");
                dialogBuilder.addCancelAction();

                dialogBuilder.setOkOperation(() -> {
                    if (form.getFieldValue(TestRunConfiguration.BUILD_NUMBER).isEmpty()) {
                        Notifier.getInstance().error("Build number is required.", "Validation Error");
                        return;
                    }

                    dialogBuilder.getDialogWrapper().close(DialogWrapper.OK_EXIT_CODE);

                    tr = DirectoryMapper.getInstance().testRunNode(newDirPath, parentDir);
                    saveSelectedToJSON(form, name, root, newDirPath, action.getProjectPanel(), tr);
                });

                dialogBuilder.show();
            });
        });

        return tr;
    }

    private void saveSelectedToJSON(final RunCreationForm form, final String runName, final CheckedTreeNode root, final Path savePath, final ProjectPanel projectPanel, final TestRunDirectoryDto tr) {
        final TestRunDto run = new TestRunDto();
        form.populateConfiguration(run);

        final String fileName = runName + ".json";
        run.setRunName(fileName);
        run.setCreatedAt(LocalDateTime.now());
        run.setStatus(TestRunStatus.CREATED);

        final List<TestRunDto.TestRunItems> items = new ArrayList<>();
        final Map<Path, List<UUID>> pathMap = new HashMap<>();

        collectCheckedItems(root, items, pathMap);
        run.setResults(items);

        final List<TestRunDto.TestCase> testCasesPaths = new ArrayList<>();
        for (final Map.Entry<Path, List<UUID>> entry : pathMap.entrySet()) {
            final TestRunDto.TestCase tcPath = new TestRunDto.TestCase();
            tcPath.setPath(entry.getKey());
            tcPath.setUuid(entry.getValue());
            testCasesPaths.add(tcPath);
        }
        run.setTestCase(testCasesPaths);

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                Files.createDirectories(savePath);
                File newJsonFile = new File(savePath.toFile(), fileName);

                Config.getMapper().writerWithDefaultPrettyPrinter().writeValue(newJsonFile, run);

                Path trMarkerPath = savePath.resolve(DirectoryType.TR.getMarker());
                if (Files.notExists(trMarkerPath))
                    Files.createFile(trMarkerPath);

                VirtualFile virtualDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(savePath.toFile());
                if (virtualDir != null)
                    virtualDir.refresh(false, true);

                ApplicationManager.getApplication().invokeLater(() -> {
                    projectPanel.getTestRunTreeBuilder().buildTree(projectPanel.getTestProjectSelector().getSelectedTestProject().getItem());
                    EditorUtil.getInstance().openEditorIfNotOpen(tr);

                });
            } catch (final Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    private void collectCheckedItems(final CheckedTreeNode node, final List<TestRunDto.TestRunItems> items, final Map<Path, List<UUID>> pathMap) {
        if (node.getUserObject() instanceof TestCaseDto tc && node.isChecked()) {
            final TestRunDto.TestRunItems item = new TestRunDto.TestRunItems();
            item.setTestCaseId(tc.getId());
            item.setStatus(TestStatus.PENDING);
            final Object rootObj = ((DefaultMutableTreeNode) node.getRoot()).getUserObject();
            item.setProject(rootObj instanceof DirectoryDto d ? d.getName() : String.valueOf(rootObj));
            items.add(item);

            Path tcPath = null;
            TreeNode parent = node.getParent();
            while (parent != null) {
                if (parent instanceof DefaultMutableTreeNode pNode) {
                    if (pNode.getUserObject() instanceof DirectoryDto dir) {
                        tcPath = dir.getPath();
                        break;
                    }
                }
                parent = parent.getParent();
            }

            if (tcPath != null) {
                pathMap.computeIfAbsent(tcPath, k -> new ArrayList<>()).add(tc.getId());
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            collectCheckedItems((CheckedTreeNode) node.getChildAt(i), items, pathMap);
        }
    }

    private CheckedTreeNode convertToCheckedNodes(final DefaultMutableTreeNode node) {
        final Object userObj = node.getUserObject();
        final CheckedTreeNode newNode = new CheckedTreeNode(userObj);
        for (int i = 0; i < node.getChildCount(); i++) {
            newNode.add(convertToCheckedNodes((DefaultMutableTreeNode) node.getChildAt(i)));
        }
        return newNode;
    }

    private DefaultMutableTreeNode buildDirectoryTree(final Path folder, final boolean isRoot, final DirectoryDto parentDir) {
        final Object label = isRoot
                ? TestRunDirectoryDto.builder().name("Test Cases (" + folder.getParent().getFileName().toString() + ")").path(folder).build()
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

    private Object resolveDirectoryObject(final Path folder, final DirectoryDto parentDir) {
        if (Files.exists(folder.resolve(DirectoryType.TSP.getMarker())))
            return DirectoryMapper.getInstance().testSetPackageNode(folder, parentDir);

        if (Files.exists(folder.resolve(DirectoryType.TS.getMarker())))
            return DirectoryMapper.getInstance().testSetNode(folder, parentDir);

        return TestRunDirectoryDto.builder().name(folder.getFileName().toString()).path(folder).build();
    }
}