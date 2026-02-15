package testGit.editorPanel.testCaseEditor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import testGit.pojo.Config;
import testGit.pojo.Directory;
import testGit.pojo.TestCase;
import testGit.util.TestCaseSorter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestCaseEditor {

    public static void open(final Directory dir) {
        System.out.println("TestCaseEditor.open() , path: " + dir);

        FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        List<TestCase> testCases = new ArrayList<>();
        File folder = dir.getFile();

        if (folder.exists() && folder.isDirectory()) {
            File[] jsonFiles = folder.listFiles((dirType, name) -> name.toLowerCase().endsWith(".json"));

            if (jsonFiles != null) {
                for (File file : jsonFiles) {
                    try {
                        TestCase tc = mapper.readValue(file, TestCase.class);
                        testCases.add(tc);
                    } catch (Exception e) {
                        System.err.println("Error parsing file: " + file.getName() + " -> " + e.getMessage());
                    }
                }
            }
        }

        testCases = TestCaseSorter.sortTestCases(testCases);

        // 1. Check if a tab for this path is already open
        for (VirtualFile openFile : editorManager.getOpenFiles()) {
            if (openFile instanceof VirtualFileImpl existing && existing.getDir().equals(dir.toString())) {
                System.out.println("open test set: " + existing.getDir());
                editorManager.openFile(existing, true);
                return;
            }
        }

        VirtualFile virtualFile = new VirtualFileImpl(dir, testCases);
        editorManager.openFile(virtualFile, true);
    }
}
