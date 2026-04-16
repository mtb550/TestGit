package testGit.editorPanel.toolBar.components;

import com.intellij.icons.AllIcons;

// TODO: cange it to pop up and list the implemented actions, excel, pdf and html
public class GenerateReportBtn extends AbstractButton {

    public GenerateReportBtn(Runnable onClickAction) {
        super("Export Results", AllIcons.ToolbarDecorator.Export);
        addActionListener(e -> onClickAction.run());
    }
}