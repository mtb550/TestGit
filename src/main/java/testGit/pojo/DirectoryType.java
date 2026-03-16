package testGit.pojo;

import com.intellij.icons.AllIcons;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.*;

@Getter
@AllArgsConstructor
public enum DirectoryType {
    PR("Project", AllIcons.Nodes.Project),
    PA("Package", AllIcons.Nodes.WebFolder),
    TS("Test Set", AllIcons.FileTypes.Text),
    TR("Test Run", AllIcons.Nodes.Services),
    TCP("Test Cases Directory", AllIcons.Nodes.Bookmark),
    TRP("Test Runs Directory", AllIcons.Nodes.Bookmark);

    private final String description;
    private final Icon icon;
}