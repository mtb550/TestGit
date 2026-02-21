package testGit.projectPanel.testRunTab;

import testGit.actions.DeletePackage;
import testGit.projectPanel.ProjectPanel;

public class ShortcutHandler {
    public static void register(final ProjectPanel projectPanel) {

        // Delete package (VK_DELETE)
        DeletePackage.register(projectPanel, projectPanel.getTestRunTree());

    }
}
