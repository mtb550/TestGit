package org.testin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.ChangesUtil;
import com.intellij.openapi.vcs.changes.LocalChangeList;

public class TestinMoveHandler {
    public void ensureFilesAreMoved(Project project) {
        ChangeListManager manager = ChangeListManager.getInstance(project);
        LocalChangeList automationList = manager.findChangeList("testin Automation");

        if (automationList == null) return;

        for (Change change : manager.getDefaultChangeList().getChanges()) {
            String path = ChangesUtil.getFilePath(change).getPath();
            if (path.contains("testin")) {
                manager.moveChangesTo(automationList, change);
            }
        }
    }
}