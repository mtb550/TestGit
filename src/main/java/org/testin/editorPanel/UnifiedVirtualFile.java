package org.testin.editorPanel;

import com.intellij.testFramework.LightVirtualFile;
import lombok.Getter;
import lombok.Setter;
import org.testin.pojo.EditorType;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.pojo.dto.TestRunDto;
import org.testin.pojo.dto.dirs.DirectoryDto;
import org.testin.pojo.dto.dirs.TestRunDirectoryDto;
import org.testin.pojo.dto.dirs.TestSetDirectoryDto;
import org.testin.projectPanel.ProjectPanel;

import javax.swing.tree.DefaultTreeModel;
import java.util.List;

@Getter
@Setter
public class UnifiedVirtualFile extends LightVirtualFile {

    // Shared Properties
    private final DirectoryDto directoryDto;
    private final List<TestCaseDto> testCaseDtos;

    // Test Run
    private ProjectPanel projectPanel;
    private DefaultTreeModel testCasesTreeModel;
    private TestRunDto metadata;
    private EditorType editorType;

    // Test Case
    public UnifiedVirtualFile(final TestSetDirectoryDto directory, final List<TestCaseDto> testCaseDtos) {
        super(directory.getName());
        this.directoryDto = directory;
        this.testCaseDtos = testCaseDtos;
        this.setFileType(FileType.TEST_CASE);
    }

    // Test Run
    public UnifiedVirtualFile(final TestRunDirectoryDto directory, final DefaultTreeModel treeModel, final List<TestCaseDto> testCaseDtos, final EditorType editorType, final ProjectPanel projectPanel) {
        super(directory.getName());
        this.directoryDto = directory;
        this.testCaseDtos = testCaseDtos;
        this.testCasesTreeModel = treeModel;
        this.editorType = editorType;
        this.projectPanel = projectPanel;
        this.setFileType(FileType.TEST_RUN);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public TestSetDirectoryDto getTestSet() {
        return directoryDto instanceof TestSetDirectoryDto ? (TestSetDirectoryDto) directoryDto : null;
    }

}