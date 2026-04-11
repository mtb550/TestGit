package testGit.editorPanel.listeners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBList;
import testGit.editorPanel.testCaseEditor.TestEditorUI;
import testGit.pojo.dto.TestCaseDto;
import testGit.util.KeyboardSet;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class KeyListener extends KeyAdapter {

    private final JBList<TestCaseDto> list;
    private final TestEditorUI ui;

    public KeyListener(final JBList<TestCaseDto> list, final TestEditorUI ui) {
        this.list = list;
        this.ui = ui;
    }

    @Override
    public void keyPressed(final KeyEvent e) {

        if (KeyboardSet.CopyTestCaseTitle.matches(e)) {
            List<TestCaseDto> selectedCases = list.getSelectedValuesList();
            if (selectedCases != null && !selectedCases.isEmpty()) {
                String titles = selectedCases.stream()
                        .map(TestCaseDto::getTitle)
                        .collect(Collectors.joining("\n"));

                StringSelection selection = new StringSelection(titles);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
            return;
        }

        if (e.getKeyCode() == KeyboardSet.DeletePackage.getKeyCode()) {
            List<TestCaseDto> selectedCases = list.getSelectedValuesList();

            if (selectedCases != null && !selectedCases.isEmpty()) {
                ui.getAllTestCaseDtos().removeAll(selectedCases);
                ui.refreshView();

                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    Path dirPath = ui.getVf().getTestSet().getPath();

                    for (TestCaseDto tc : selectedCases) {
                        try {
                            Files.deleteIfExists(dirPath.resolve(tc.getId() + ".json"));
                        } catch (Exception ex) {
                            System.err.println("Failed to delete test case JSON: " + tc.getId());
                        }
                    }

                    ApplicationManager.getApplication().invokeLater(ui::updateSequenceAndSaveAll);
                });
            }
        }
    }
}