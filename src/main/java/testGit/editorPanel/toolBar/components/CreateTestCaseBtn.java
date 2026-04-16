package testGit.editorPanel.toolBar.components;

import com.intellij.icons.AllIcons;

public class CreateTestCaseBtn extends AbstractButton {

    public CreateTestCaseBtn(Runnable onClickAction) {
        super("Add Test Case", AllIcons.General.Add);

        addActionListener(e -> onClickAction.run());
    }
}