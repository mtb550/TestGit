package org.testin.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.Config;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.util.KeyboardSet;

import java.awt.datatransfer.StringSelection;
import java.util.List;

public class CopyTestCaseNode extends DumbAwareAction {
    private final JBList<TestCaseDto> list;

    public CopyTestCaseNode(final JBList<TestCaseDto> list) {
        super("Copy Node", "Copy selected test case(s) to clipboard", AllIcons.Actions.Copy);
        this.list = list;
        this.registerCustomShortcutSet(KeyboardSet.CopyTestCaseNode.getCustomShortcut(), list);

    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("[DEBUG] CopyTestCaseNode: actionPerformed triggered.");

        List<TestCaseDto> selectedTestCases = list.getSelectedValuesList();
        System.out.println("[DEBUG] CopyTestCaseNode: Selected items count = " + selectedTestCases.size());

        if (!selectedTestCases.isEmpty()) {
            try {
                System.out.println("[DEBUG] CopyTestCaseNode: Attempting to serialize with Jackson...");
                ObjectMapper mapper = Config.getMapper();
                String json = mapper.writeValueAsString(selectedTestCases);

                System.out.println("[DEBUG] CopyTestCaseNode: Serialization successful. JSON length = " + json.length());

                CopyPasteManager.getInstance().setContents(new StringSelection(json));
                System.out.println("[DEBUG] CopyTestCaseNode: Data successfully pushed to System Clipboard.");
            } catch (Exception ex) {
                System.err.println("[ERROR] CopyTestCaseNode: Failed to serialize clipboard contents: " + ex.getMessage());
                ex.printStackTrace(System.err);
            }
        } else {
            System.out.println("[DEBUG] CopyTestCaseNode: Aborted. No items were selected in the JBList.");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Left empty for testing functionality. Button will always be enabled.
    }
}