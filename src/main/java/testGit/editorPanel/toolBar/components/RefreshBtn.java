package testGit.editorPanel.toolBar.components;

import com.intellij.icons.AllIcons;

// TODO: change Runnable to Consumer or BiConsumer
public class RefreshBtn extends AbstractButton {
    public RefreshBtn(Runnable onRefreshAction) {
        super("Refresh", AllIcons.Actions.Refresh);
        addActionListener(e -> onRefreshAction.run());
    }
}