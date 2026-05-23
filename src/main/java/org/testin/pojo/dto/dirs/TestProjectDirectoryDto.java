package org.testin.pojo.dto.dirs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.testin.pojo.CreateNodeMenu;
import org.testin.pojo.ProjectStatus;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TestProjectDirectoryDto extends DirectoryDto {
    private ProjectStatus projectStatus;

    private TestCasesMainDirectoryDto testCasesDirectory;

    private TestRunsMainDirectoryDto testRunsDirectory;

    private String pathName;

    @Override
    public CreateNodeMenu getMenu() {
        return CreateNodeMenu.TEST_PROJECT;
    }

}
