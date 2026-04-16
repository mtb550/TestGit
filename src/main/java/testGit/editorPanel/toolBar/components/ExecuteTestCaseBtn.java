package testGit.editorPanel.toolBar.components;

import com.intellij.icons.AllIcons;

public class ExecuteTestCaseBtn extends AbstractButton {

    public ExecuteTestCaseBtn(Runnable onClickAction) {
        super("Start", AllIcons.Nodes.Services);
        addActionListener(e -> onClickAction.run());
    }
}