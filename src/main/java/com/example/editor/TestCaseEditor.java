package com.example.editor;

import com.example.demo.TestCaseVirtualFile;
import com.example.pojo.Config;
import com.example.pojo.TestCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestCaseEditor {

    public static void open(Path featurePath) {
        FileEditorManager editorManager = FileEditorManager.getInstance(Config.getProject());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        List<TestCase> testCases = new ArrayList<>();
        File folder = featurePath.toFile();

        if (folder.exists() && folder.isDirectory()) {
            File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

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

        testCases.sort(Comparator.comparingInt(TestCase::getSort));

        if (testCases.isEmpty()) {
            System.out.println("No test cases found in path: " + featurePath);
            return;
        }

        // 1. التحقق مما إذا كان هناك تبويب مفتوح لهذا المسار (Feature Path)
        for (VirtualFile openFile : editorManager.getOpenFiles()) {
            // نفترض أننا قمنا بتحديث getModulePath() لتعيد String أو Path
            if (openFile instanceof TestCaseVirtualFile existing &&
                    existing.getFeaturePath().equals(featurePath.toString())) {

                editorManager.openFile(existing, true);
                return;
            }
        }

// 2. فتح تبويب محرر جديد باستخدام مسار المجلد وقائمة حالات الاختبار
// قمنا باستبدال moduleId بـ featurePath.toString()
        VirtualFile virtualFile = new TestCaseVirtualFile(featurePath.toString(), testCases);
        editorManager.openFile(virtualFile, true);
    }
}
