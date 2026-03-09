package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.fileEditor.FileEditorManager;
import testGit.pojo.*;
import testGit.projectPanel.ProjectPanel;
import testGit.util.DirectoryMapper;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestRunEditor {

    private static DefaultTreeModel createFilteredModel(List<TestCase> cases) {
        System.out.println("TestRunEditor.createFilteredModel()");
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Selected Test Cases");
        cases.forEach(tc -> root.add(new DefaultMutableTreeNode(tc)));
        return new DefaultTreeModel(root);
    }

    public static void open(Path runFilePath, ProjectPanel projectPanel) {
        System.out.println("TestRunEditor.open()");

        try {
            TestRun metadata = Config.getMapper().readValue(runFilePath.toFile(), TestRun.class);
            List<TestCase> loadedCases = new ArrayList<>();

            if (metadata.getResults() != null) {
                Set<String> targetIds = metadata.getResults().stream()
                        .map(item -> item.getTestCaseId().toString())
                        .collect(Collectors.toSet());

                String projectName = projectPanel.getTestProjectSelector().getSelectedTestProject().getItem().getFileName();
                Path testCasesRoot = Config.getTestGitPath().resolve(projectName).resolve("testCases");

                if (Files.exists(testCasesRoot)) {
                    try (java.util.stream.Stream<Path> paths = java.nio.file.Files.walk(testCasesRoot)) {
                        paths.filter(java.nio.file.Files::isRegularFile)
                                .filter(p -> p.toString().endsWith(".json"))
                                .forEach(p -> {
                                    try {
                                        TestCase tc = Config.getMapper().readValue(p.toFile(), TestCase.class);
                                        if (tc.getId() != null && targetIds.contains(tc.getId())) {
                                            loadedCases.add(tc);
                                        }
                                    } catch (Exception ignored) {
                                    }
                                });
                    }
                }
            }

            List<TestCase> sortedCases = testGit.util.TestCaseSorter.sortTestCases(loadedCases);
            System.out.println("[TRACE] Opened run. Loaded " + sortedCases.size() + " test cases.");

            VirtualFileImpl virtualFile = new VirtualFileImpl(
                    runFilePath.toAbsolutePath().toString(),
                    createFilteredModel(sortedCases),
                    sortedCases,
                    EditorType.TEST_RUN_OPENING,
                    projectPanel
            );
            virtualFile.setMetadata(metadata);

            FileEditorManager.getInstance(Config.getProject()).openFile(virtualFile, true);
        } catch (IOException e) {
            System.err.println("Failed to open Test Run: " + e.getMessage());
        }
    }

    public static void create(Path runFilePath, ProjectPanel projectPanel, Directory projectName, TestRun metadata) {
        System.out.println("[TRACE] TestRunEditor.create() for project: " + projectName);

        Path testCasesPath = Config.getTestGitPath().resolve(projectName.getFileName()).resolve("testCases");
        File rootFolder = testCasesPath.toFile();

        DefaultMutableTreeNode rootNode = buildDirectoryTree(rootFolder, true);
        DefaultTreeModel fullTestCasesModel = new DefaultTreeModel(rootNode);

        List<TestCase> initialTestCases = new ArrayList<>();

        VirtualFileImpl virtualFile = new VirtualFileImpl(
                runFilePath.toAbsolutePath().toString(),
                fullTestCasesModel,
                initialTestCases,
                EditorType.TEST_RUN_CREATION,
                projectPanel
        );

        virtualFile.setMetadata(metadata);
        FileEditorManager.getInstance(Config.getProject()).openFile(virtualFile, true);
    }

    private static DefaultMutableTreeNode buildDirectoryTree(File folder, boolean isRoot) {
        Object userObject;
        if (isRoot) {
            userObject = "Test Cases (" + folder.getParentFile().getName() + ")";
        } else {
            userObject = DirectoryMapper.map(folder);
            if (userObject == null) {
                userObject = folder.getName();
            }
        }

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);

        File[] children = folder.listFiles();
        if (children != null) {
            Arrays.sort(children, (a, b) -> {
                if (a.isDirectory() && !b.isDirectory()) return -1;
                if (!a.isDirectory() && b.isDirectory()) return 1;
                return a.getName().compareToIgnoreCase(b.getName());
            });

            for (File child : children) {
                if (child.isDirectory()) {
                    node.add(buildDirectoryTree(child, false));
                } else if (child.isFile() && child.getName().endsWith(".json")) {
                    try {
                        TestCase tc = Config.getMapper().readValue(child, TestCase.class);
                        node.add(new DefaultMutableTreeNode(tc));
                    } catch (Exception e) {
                        System.err.println("[TRACE-ERROR] Failed to parse JSON: " + child.getName());
                    }
                }
            }
        }

        return node;
    }
}