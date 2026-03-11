package testGit.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DirectoryType {
    PR("Project"),
    PA("Package"),
    TS("Test Set"),
    TR("Test Run");

    private final String description;
}