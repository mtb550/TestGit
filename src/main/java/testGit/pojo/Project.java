package testGit.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.nio.file.Path;

@Setter
@Getter
@NoArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project extends Directory {
    private ProjectStatus projectStatus;

    private File testCasePath;

    private File testRunPath;

    @Override
    public Project setName(String name) {
        super.name = name;
        return this;
    }

    @Override
    public Project setFilePath(Path filePath) {
        super.setFilePath(filePath);
        return this;
    }

    @Override
    public Project setFile(File file) {
        super.setFile(file);
        return this;
    }

    @Override
    public Project setFileName(String fileName) {
        super.setFileName(fileName);
        return this;
    }
}
