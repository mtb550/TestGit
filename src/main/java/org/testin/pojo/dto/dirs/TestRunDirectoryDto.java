package org.testin.pojo.dto.dirs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.testin.pojo.CreateNodeMenu;
import org.testin.pojo.TestRunStatus;

import java.util.concurrent.atomic.AtomicBoolean;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class TestRunDirectoryDto extends DirectoryDto {
    private final AtomicBoolean isLoadingStatus = new AtomicBoolean(false); // todo, check it what the purpose

    private volatile TestRunStatus runStatus;

    @Override
    public CreateNodeMenu getMenu() {
        return CreateNodeMenu.TEST_RUN;
    }
}
