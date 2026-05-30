package org.testin.actions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAwareAction;
import org.jetbrains.annotations.NotNull;
import org.testin.editorPanel.IEditorUI;
import org.testin.editorPanel.testCaseEditor.TestEditorUI;
import org.testin.pojo.Config;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.util.KeyboardSet;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PasteTestCaseNode extends DumbAwareAction {
    private final IEditorUI editorUI;

    public PasteTestCaseNode(final IEditorUI editorUI, final JComponent component) {
        super("Paste Node", "Paste selected test cases from clipboard to the end of the list", AllIcons.Actions.MenuPaste);
        this.editorUI = editorUI;

        this.registerCustomShortcutSet(KeyboardSet.PasteTestCaseNode.getCustomShortcut(), component);
    }

    @Override
    public void actionPerformed(final @NotNull AnActionEvent e) {
        System.out.println("[DEBUG] PasteNode: actionPerformed triggered.");

        List<TestCaseDto> pastedCases = getFromClipboard();
        System.out.println("[DEBUG] PasteNode: Parsed " + pastedCases.size() + " test cases from clipboard.");

        if (pastedCases.isEmpty()) return;

        ApplicationManager.getApplication().invokeLater(() -> {
            System.out.println("[DEBUG] PasteNode: Inside invokeLater. Preparing to clone items.");
            for (TestCaseDto tc : pastedCases) {

                if (tc == null) {
                    continue;
                }

                TestCaseDto clonedTc = cloneForPasting(tc);
                editorUI.getAllTestCases().add(clonedTc);
                System.out.println("[DEBUG] PasteNode: Cloned and added new test case ID: " + clonedTc.getId());
            }

            if (!(editorUI instanceof TestEditorUI ui)) {
                System.out.println("[DEBUG] PasteNode: editorUI is not an instance of TestEditorUI. Aborting sequence save.");
                return;
            }

            System.out.println("[DEBUG] PasteNode: Triggering sequence update and save...");
            ui.sortAndIdentifyUnsorted();
            ui.updateSequenceAndSaveAll();

            System.out.println("Successfully pasted and saved " + pastedCases.size() + " test cases.");
        });
    }

    @Override
    public void update(final @NotNull AnActionEvent e) {
        // Left empty for testing functionality. Button will always be enabled.
    }

    private List<TestCaseDto> getFromClipboard() {
        System.out.println("[DEBUG] PasteNode: Reading clipboard contents...");
        Transferable contents = CopyPasteManager.getInstance().getContents();

        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String json = (String) contents.getTransferData(DataFlavor.stringFlavor);

                System.out.println("[DEBUG] PasteNode: Parsing clipboard text with Jackson...");
                ObjectMapper mapper = Config.getMapper();

                return mapper.readValue(json, new TypeReference<>() {
                });

            } catch (Exception ex) {
                System.out.println("[DEBUG] PasteNode: Clipboard text is not a valid JSON TestCase array.");
                System.err.println("[WARNING] Failed to parse clipboard JSON: " + ex.getMessage());
            }
        }

        return Collections.emptyList();
    }

    private TestCaseDto cloneForPasting(final TestCaseDto original) {
        final ZonedDateTime now = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        final TestCaseDto clonedTc = Config.getMapper().convertValue(original, TestCaseDto.class);

        clonedTc.setId(UUID.randomUUID())
                .setDescription(original.getDescription() + " (Copy)")
                .setCreatedAt(now)
                .setUpdatedAt(now);

        return clonedTc;
    }
}