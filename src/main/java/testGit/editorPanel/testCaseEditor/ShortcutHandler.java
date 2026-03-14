package testGit.editorPanel.testCaseEditor;

import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import testGit.actions.*;
import testGit.pojo.TestCase;
import testGit.pojo.TestPackage;


public class ShortcutHandler {
    public static void register(TestPackage dir, JBList<TestCase> list, CollectionListModel<TestCase> model) {

        new CreateTestCase(dir, list, model);
        new RemoveTestCase(dir, list, model);
        new OpenTestCaseDetails(list);
        new ShowTestCaseContextMenu(dir, list, model);
        new CloseTestCaseDetails(list);

    }


}