package com.example.editor;

import com.example.Runner.TestNGRunnerByClassName;
import com.example.pojo.TestCase;
import com.example.util.ActionHistory;
import com.example.util.Notifier;
import com.example.util.Tools;
import com.example.viewer.TestCaseToolWindow;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class TableContextMenu {

    public static JBPopupMenu create(String featurePath, @NotNull VirtualFile file, JList<TestCase> list,
                                     DefaultListModel<TestCase> model,
                                     TestCase tc) {
        System.out.println("TableContextMenu.create()");

        JBPopupMenu menu = new JBPopupMenu();

        JBMenuItem copyItem = new JBMenuItem("📋 Copy");
        copyItem.addActionListener(e -> {
            String text = "Title: " + tc.getTitle() + "\nSteps: " + tc.getSteps() + "\nExpected: " + tc.getExpectedResult();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        });
        menu.add(copyItem);

        JBMenuItem runItem = new JBMenuItem("▶ Run Test");
        runItem.addActionListener(e -> {
            String ref = tc.getAutomationRef();
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            if (ref != null && !ref.isBlank()) {
                Tools.printTestSourceRoots(project);
                TestNGRunnerByClassName.runTestClass(project, ref);
                Notifier.notify(project, "Test Case Notifications", "Running TestNG class: ", ref, NotificationType.INFORMATION);
            } else {
                Notifier.notify(project,
                        "No automation reference found for this test case.",
                        "", "",
                        NotificationType.WARNING);
            }
        });
        menu.add(runItem);

        JBMenuItem viewItem = new JBMenuItem("🔍 View Details");
        viewItem.addActionListener(e -> TestCaseToolWindow.show(tc));
        menu.add(viewItem);

        JBMenuItem deleteItem = new JBMenuItem("🗑 Delete");
        deleteItem.addActionListener(e -> {
            int idx = model.indexOf(tc);
            if (idx >= 0) {
                model.remove(idx);
            }
        });
        menu.add(deleteItem);

        // === 📝 Add Undo button ===
        JBMenuItem undoItem = new JBMenuItem("↩ Undo");
        undoItem.addActionListener(e -> {
            ActionHistory.undo();
        });
        menu.add(undoItem);

        // === 🔁 Add Redo button ===
        JBMenuItem redoItem = new JBMenuItem("↪ Redo");
        redoItem.addActionListener(e -> {
            ActionHistory.redo();
        });
        menu.add(redoItem);

        // === ➕ Add Test Case button
        JBMenuItem addItem = new JBMenuItem("➕ Add Test Case");
        addItem.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(list,
                    "Enter title for new test case:",
                    "New Test Case",
                    JOptionPane.PLAIN_MESSAGE);

            if (title != null && !title.trim().isEmpty()) {
                TestCase newCase = new TestCase();
                newCase.setTitle(title.trim());
                //newCase.setSteps("Step 1: ...");
                //newCase.setExpectedResult("Expected result...");
                newCase.setPriority("LOW");
                //newCase.setAutomationRef("");
                newCase.setSort(model.getSize() + 1);
                newCase.setId(UUID.randomUUID().toString());

                /// here create json file
                ObjectMapper mapper = new ObjectMapper();
                try {
                    // issue in path
                    System.out.println(file.getCanonicalPath());
                    System.out.println(file.getCanonicalFile());
                    System.out.println(featurePath);
                    mapper.registerModule(new JavaTimeModule());
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    File targetFile = new File(featurePath, "testcase_" + newCase.getId() + ".json");
                    mapper.writeValue(targetFile, newCase);
                    // Refresh the IDE so the file appears in the project tree
                    com.intellij.openapi.vfs.LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                System.out.println("JSON file created successfully!");

                model.addElement(newCase);
                list.ensureIndexIsVisible(model.getSize() - 1);
                list.setSelectedIndex(model.getSize() - 1);
            }
        });
        menu.add(addItem);


        return menu;
    }

}
