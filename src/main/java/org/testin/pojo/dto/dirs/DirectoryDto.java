package org.testin.pojo.dto.dirs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.testin.pojo.CreateNodeMenu;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString()
public abstract class DirectoryDto {
    private String name;

    private Path path;

    private List<String> fqcn;

    @ToString.Exclude
    private DirectoryDto parent;

    @JsonAlias("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonAlias("created_by")
    private String createdBy;

    @JsonAlias("modified_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    @JsonAlias("modified_by")
    private String modifiedBy;

    @ToString.Include(name = "parentName")
    private String getParentNameForToString() {
        return parent != null ? parent.getName() : "null";
    }

    public abstract CreateNodeMenu getMenu();
}
