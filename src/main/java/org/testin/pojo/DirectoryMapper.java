package org.testin.pojo;

import org.testin.pojo.dto.dirs.*;
import org.testin.util.Tools;
import org.testin.util.notifications.Notifier;

import java.nio.file.Path;
import java.util.List;

public class DirectoryMapper {

    private static final DirectoryMapper INSTANCE = new DirectoryMapper();

    private DirectoryMapper() {
    }

    public static DirectoryMapper getInstance() {
        return INSTANCE;
    }

    public TestProjectDirectoryDto testProjectNode(final Path path) { // todo, path is Testin path , no need to pass the path here.
        final String fileName = path.getFileName().toString();
        try {
            final TestProjectDirectoryDto testProjectDirectoryDto = TestProjectDirectoryDto.builder()
                    .name(fileName)
                    .path(path)
                    .pathName(fileName)
                    .fqcn(List.of(fileName))
                    //.projectStatus(ProjectStatus.valueOf(parts[1])) // todo, to be moved to .pr file
                    .build();

            TestCasesMainDirectoryDto tcd = TestCasesMainDirectoryDto.builder()
                    .path(path.resolve(DirectoryType.TCD.getPathName()))
                    .name(DirectoryType.TCD.getDisplayedName())
                    .fqcn(List.of(fileName, DirectoryType.TCD.getPathName()))
                    .parent(testProjectDirectoryDto)
                    .build();

            TestRunsMainDirectoryDto trd = TestRunsMainDirectoryDto.builder()
                    .path(path.resolve(DirectoryType.TRD.getPathName()))
                    .name(DirectoryType.TRD.getDisplayedName())
                    .fqcn(List.of(fileName, DirectoryType.TRD.getPathName()))
                    .parent(testProjectDirectoryDto)
                    .build();

            testProjectDirectoryDto.setTestCasesDirectory(tcd);
            testProjectDirectoryDto.setTestRunsDirectory(trd);

            System.out.println("retrieve the project directory: " + testProjectDirectoryDto);
            return testProjectDirectoryDto;

        } catch (Exception e) {
            Notifier.getInstance().error("Read Test Project Failed", "Skipping invalid format: " + fileName);
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public TestCasesMainDirectoryDto testCasesRootNode(final Path path, final TestProjectDirectoryDto parent) {
        final String fileName = path.getFileName().toString();
        try {
            TestCasesMainDirectoryDto testCasesMainDirectoryDto = TestCasesMainDirectoryDto
                    .builder()
                    .name(path.getFileName().toString())
                    .path(path)
                    .parent(parent)
                    .fqcn(Tools.getInstance().appendFqcn(parent.getFqcn(), fileName))
                    .build();

            System.out.println("retrieve the test cases main directory: " + testCasesMainDirectoryDto);
            return testCasesMainDirectoryDto;

        } catch (Exception e) {
            Notifier.getInstance().error("Read Test Case Package Failed", "Failed to parse directory: " + path.getFileName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public TestRunsMainDirectoryDto testRunsRootNode(final Path path, final TestProjectDirectoryDto parent) {
        final String fileName = path.getFileName().toString();
        try {
            TestRunsMainDirectoryDto testRunsMainDirectoryDto = TestRunsMainDirectoryDto
                    .builder()
                    .name(path.getFileName().toString())
                    .path(path)
                    .parent(parent)
                    .fqcn(Tools.getInstance().appendFqcn(parent.getFqcn(), fileName))
                    .build();

            System.out.println("retrieve the test runs main directory: " + testRunsMainDirectoryDto);
            return testRunsMainDirectoryDto;

        } catch (Exception e) {
            Notifier.getInstance().error("Read Test Case Package Failed", "Failed to parse directory: " + path.getFileName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public TestSetPackageDirectoryDto testSetPackageNode(final Path path, final DirectoryDto parent) {
        final String fileName = path.getFileName().toString();
        try {
            TestSetPackageDirectoryDto testSetPackageDirectoryDto = TestSetPackageDirectoryDto
                    .builder()
                    .name(fileName)
                    .path(path)
                    .parent(parent)
                    .fqcn(Tools.getInstance().appendFqcn(parent.getFqcn(), fileName))
                    .build();

            System.out.println("retrieve the test set package directory: " + testSetPackageDirectoryDto);
            return testSetPackageDirectoryDto;

        } catch (Exception e) {
            Notifier.getInstance().error("Read Test Case Package Failed", "Failed to parse directory: " + path.getFileName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public TestRunPackageDirectoryDto testRunPackageNode(final Path path, final DirectoryDto parent) {
        final String fileName = path.getFileName().toString();
        try {
            TestRunPackageDirectoryDto testRunPackageDirectoryDto = TestRunPackageDirectoryDto
                    .builder()
                    .name(fileName)
                    .path(path)
                    .parent(parent)
                    .fqcn(Tools.getInstance().appendFqcn(parent.getFqcn(), fileName))
                    .build();

            System.out.println("retrieve the test run package directory: " + testRunPackageDirectoryDto);
            return testRunPackageDirectoryDto;

        } catch (Exception e) {
            Notifier.getInstance().error("Read Test Run Package Failed", "Failed to parse directory: " + path.getFileName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public TestSetDirectoryDto testSetNode(final Path path, final DirectoryDto parent) {
        final String fileName = path.getFileName().toString();
        try {
            TestSetDirectoryDto testSetDirectoryDto = TestSetDirectoryDto
                    .builder()
                    .name(fileName)
                    .path(path)
                    .parent(parent)
                    .fqcn(Tools.getInstance().appendFqcn(parent.getFqcn(), fileName))
                    .build();

            System.out.println("retrieve the test set directory: " + testSetDirectoryDto);
            return testSetDirectoryDto;

        } catch (Exception e) {
            Notifier.getInstance().error("Read Test Set Failed", "Failed to parse directory: " + path.getFileName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }

    public TestRunDirectoryDto testRunNode(final Path path, final DirectoryDto parent) {
        final String fileName = path.getFileName().toString();
        try {
            TestRunDirectoryDto testRunDirectoryDto = TestRunDirectoryDto
                    .builder()
                    .name(fileName)
                    .path(path)
                    .parent(parent)
                    .fqcn(Tools.getInstance().appendFqcn(parent.getFqcn(), fileName))
                    .build();

            System.out.println("retrieve the test run directory: " + testRunDirectoryDto);
            return testRunDirectoryDto;

        } catch (Exception e) {
            Notifier.getInstance().error("Read Test Run Failed", "Failed to parse directory: " + path.getFileName());
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }
}