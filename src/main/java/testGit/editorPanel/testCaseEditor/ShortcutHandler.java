package testGit.editorPanel.testCaseEditor;

import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import testGit.actions.*;
import testGit.pojo.Package;
import testGit.pojo.TestCase;


public class ShortcutHandler {
    public static void register(Package dir, JBList<TestCase> list, CollectionListModel<TestCase> model) {

        new CreateTestCase(dir, list, model);
        new RemoveTestCase(dir, list, model);
        new OpenTestCaseDetails(list);
        new ShowTestCaseContextMenu(dir, list, model);
        new CloseTestCaseDetails(list);

    }


}