package testGit.util;

import testGit.pojo.*;

import java.io.File;

public class DirectoryMapper {
    public static TestPackage mapPackage(File file) {
        try {
            String[] parts = file.getName().replaceFirst("\\.json$", "").split("_", 3);

            return new TestPackage()
                    .setPackageType(PackageType.valueOf(parts[0]))
                    .setIcon(DirectoryIcon.valueOf(parts[0]))
                    .setName(parts[1])
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName());
        } catch (Exception e) {
            Notifier.error("Read Directory Failed", "Skipping invalid directory format: " + file.getName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public static TestProject mapProject(File file) {
        try {
            String[] parts = file.getName().split("_", 2);

            return new TestProject()
                    .setTestCase(
                            new TestPackage()
                                    .setIcon(DirectoryIcon.TCP)
                                    .setFileName("TCP_testCases")
                                    .setPackageType(PackageType.TCP)
                                    .setFile(file.toPath().resolve("TCP_testCases").toFile())
                                    .setFilePath(file.toPath().resolve("TCP_testCases"))
                                    .setName("Test Cases")
                    )
                    .setTestRun(
                            new TestPackage()
                                    .setIcon(DirectoryIcon.TRP)
                                    .setFileName("TRP_testRuns")
                                    .setPackageType(PackageType.TRP)
                                    .setFile(file.toPath().resolve("TRP_testRuns").toFile())
                                    .setFilePath(file.toPath().resolve("TRP_testRuns"))
                                    .setName("Test Runs")
                    )
                    .setProjectStatus(ProjectStatus.valueOf(parts[1]))
                    .setName(parts[0])
                    .setFile(file)
                    .setFilePath(file.toPath())
                    .setFileName(file.getName())
                    .setIcon(DirectoryIcon.PR);
        } catch (Exception e) {
            Notifier.error("Read Directory Failed", "Skipping invalid directory format: " + file.getName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }
}
