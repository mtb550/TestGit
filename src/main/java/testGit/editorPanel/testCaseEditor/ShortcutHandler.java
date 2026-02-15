package testGit.editorPanel.testCaseEditor;

import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import testGit.actions.CreateTestCase;
import testGit.actions.DeleteTestCase;
import testGit.pojo.Directory;
import testGit.pojo.TestCase;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ShortcutHandler {
    public static void register(final Directory dir, final JBList<TestCase> list, final CollectionListModel<TestCase> model) {

        // Add
        KeyStroke ctrlM = KeyStroke.getKeyStroke("control M");
        CreateTestCase addAction = new CreateTestCase(dir, list, model);
        addAction.registerCustomShortcutSet(new CustomShortcutSet(ctrlM), list);

        // Delete
        KeyStroke deleteKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        DeleteTestCase deleteAction = new DeleteTestCase(dir, list, model);
        deleteAction.registerCustomShortcutSet(new CustomShortcutSet(deleteKey), list);
    }
}