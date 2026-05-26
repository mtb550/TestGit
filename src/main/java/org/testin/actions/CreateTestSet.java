package org.testin.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleTree;
import org.testin.pojo.Config;
import org.testin.pojo.DirectoryMapper;
import org.testin.pojo.DirectoryType;
import org.testin.pojo.NodeCreator;
import org.testin.pojo.dto.dirs.DirectoryDto;
import org.testin.pojo.dto.dirs.TestSetDirectoryDto;
import org.testin.util.EditorUtil;
import org.testin.util.Tools;
import org.testin.util.TreeUtilImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.IOException;
import java.nio.file.Path;

public class CreateTestSet implements NodeCreator {

    @Override
    public DirectoryDto execute(final CreateTestNode action, final Project project, final String name, final DefaultMutableTreeNode parentNode, final DirectoryDto parentDir, final Path newDirPath) {
        TestSetDirectoryDto ts = DirectoryMapper.getInstance().testSetNode(newDirPath, parentDir);

        TreeUtilImpl.createVf(this, parentDir.getPath(), ts.getName());
        TreeUtilImpl.createDataVf(this, newDirPath, DirectoryType.TS.getMarker());
        TreeUtilImpl.createNode(action.getTree(), parentNode, ts);

        Tools.getInstance().createJavaClassInTestRoot(project, parentDir.getName(), name);
        EditorUtil.getInstance().openEditor(ts);

        return ts;
    }

    public VirtualFile inBackground(final Object requestor, final VirtualFile targetDirectory, final DirectoryDto parentDirDto, final DefaultMutableTreeNode parentNode, final SimpleTree tree, final String name) throws IOException {
        String safeDirName = name.replaceAll("[\\\\/:*?\"<>|]", "_");

        VirtualFile sheetDir = targetDirectory.findChild(safeDirName);
        boolean isNewDirCreated = false;

        if (sheetDir == null) {
            sheetDir = targetDirectory.createChildDirectory(requestor, safeDirName);
            isNewDirCreated = true;

            TestSetDirectoryDto newTsDto = TestSetDirectoryDto
                    .builder()
                    .name(safeDirName)
                    .path(parentDirDto.getPath().resolve(safeDirName))
                    .build();

            TreeUtilImpl.createNode(tree, parentNode, newTsDto);
            Tools.getInstance().createJavaClassInTestRoot(Config.getProject(), parentDirDto.getName(), safeDirName);
        }

        if (sheetDir.findChild(DirectoryType.TS.getMarker()) == null) {
            sheetDir.createChildData(requestor, DirectoryType.TS.getMarker());
        }

        if (isNewDirCreated && tree != null && tree.getModel() instanceof DefaultTreeModel treeModel) {
            treeModel.reload(parentNode);
            tree.updateUI();
            tree.revalidate();
        }

        return sheetDir;
    }
}