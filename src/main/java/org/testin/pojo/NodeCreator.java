package org.testin.pojo;

import com.intellij.openapi.project.Project;
import org.testin.actions.CreateTestNode;
import org.testin.pojo.dto.dirs.DirectoryDto;

import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Path;

@FunctionalInterface
public interface NodeCreator {
    DirectoryDto execute(final CreateTestNode action, final Project project, final String name, final DefaultMutableTreeNode parentNode, final DirectoryDto parentDir, final Path newDirPath);
}
