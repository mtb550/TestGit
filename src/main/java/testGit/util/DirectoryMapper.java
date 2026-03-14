package testGit.util;

import testGit.pojo.DirectoryType;
import testGit.pojo.ProjectStatus;
import testGit.pojo.TestPackage;
import testGit.pojo.TestProject;

import java.io.File;

public class DirectoryMapper {
    public static TestPackage map(File file) {
        try {
            String[] parts = file.getName().replaceFirst("\\.json$", "").split("_", 3);

            return new TestPackage()
                    .setDirectoryType(DirectoryType.valueOf(parts[0]))
                    .setName(parts[1])
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName())

                    ;
        } catch (Exception e) {
            Notifier.error("Read Directory Failed", "Skipping invalid directory format: " + file.getName());
            return null;
        }
    }

    public static TestProject mapProject(File file) {
        try {
            String[] parts = file.getName().split("_", 2);

            return new TestProject()
                    .setProjectStatus(ProjectStatus.valueOf(parts[1]))
                    .setName(parts[0])
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName());
        } catch (Exception e) {
            Notifier.error("Read Directory Failed", "Skipping invalid directory format: " + file.getName());
            return null;
        }
    }
}
