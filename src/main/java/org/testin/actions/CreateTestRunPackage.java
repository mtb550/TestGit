package org.testin.actions;

import com.intellij.openapi.project.Project;
import org.testin.pojo.DirectoryType;
import org.testin.pojo.NodeCreator;
import org.testin.pojo.dto.dirs.DirectoryDto;
import org.testin.pojo.dto.dirs.TestRunPackageDirectoryDto;
import org.testin.util.Tools;
import org.testin.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Path;

public class CreateTestRunPackage implements NodeCreator {

    @Override
    public void execute(final CreateTestNode action, final Project project, final String name, final DefaultMutableTreeNode parentNode, final DirectoryDto parentDir, final Path newDirPath) {
        TestRunPackageDirectoryDto newTestRunPackageDirectory = TestRunPackageDirectoryDto
                .builder()
                .name(name)
                .path(parentDir.getPath().resolve(name))
                .parent(parentDir)
                .fqcn(Tools.getInstance().appendFqcn(parentDir.getFqcn(), name))
                .build();

        TreeUtilImpl.createVf(this, parentDir.getPath(), name);
        TreeUtilImpl.createNode(action.getTree(), parentNode, newTestRunPackageDirectory);
        TreeUtilImpl.createDataVf(this, newDirPath, DirectoryType.TRP.getMarker());
    }
}