package org.testin.pojo.dto.dirs;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.testin.pojo.CreateNodeMenu;
import org.testin.pojo.TestProjectMarker;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TestProjectDirectoryDto extends DirectoryDto {

    private TestCasesMainDirectoryDto testCasesDirectory;

    private TestRunsMainDirectoryDto testRunsDirectory;

    private String pathName;

    private TestProjectMarker marker;

    @Override
    public CreateNodeMenu getMenu() {
        return CreateNodeMenu.TEST_PROJECT;
    }

}
