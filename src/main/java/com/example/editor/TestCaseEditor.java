package com.example.editor;

import com.example.demo.TestCaseVirtualFile;
import com.example.pojo.TestCase;
import com.example.util.sql;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

public class TestCaseEditor {

    public static void open(int moduleId) {
        sql DB = new sql();

        Project ideProject = ProjectManager.getInstance().getOpenProjects()[0];
        FileEditorManager editorManager = FileEditorManager.getInstance(ideProject);

        List<TestCase> testCases = List.of(DB.get(
                "SELECT * FROM nafath_tc WHERE module = ? ORDER BY sort", moduleId
        ).as(TestCase[].class));

        if (testCases.isEmpty()) {
            System.out.println("No test cases found for module ID: " + moduleId);
            return;
        }

        // Check if a tab for this module ID is already open
        for (VirtualFile openFile : editorManager.getOpenFiles()) {
            if (openFile instanceof TestCaseVirtualFile existing &&
                    existing.getModuleId() == moduleId) {
                editorManager.openFile(existing, true);
                return;
            }
        }

        // Open a new editor tab for this module ID
        VirtualFile virtualFile = new TestCaseVirtualFile(moduleId, testCases);
        editorManager.openFile(virtualFile, true);
    }
}
