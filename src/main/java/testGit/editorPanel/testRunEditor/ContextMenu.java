package testGit.editorPanel.testRunEditor;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import testGit.actions.RunTestCase;
import testGit.actions.ViewDetails;
import testGit.pojo.TestCase;
import testGit.pojo.TestPackage;

public class ContextMenu extends DefaultActionGroup {
    public ContextMenu(TestPackage dir, JBList<TestCase> list, CollectionListModel<TestCase> model, TestCase tc) {
        super("Test Run Actions", false);
        System.out.println("testGit.editorPanel.testRunEditor.ContextMenu()");

        add(new ViewDetails(tc));
        addSeparator();
        add(new RunTestCase(tc, list));


    }
}