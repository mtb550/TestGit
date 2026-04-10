package testGit.util.reports;

import com.intellij.openapi.application.ApplicationManager;
import testGit.pojo.Config;
import testGit.pojo.dto.TestRunDto;
import testGit.pojo.dto.dirs.TestRunDirectoryDto;
import testGit.util.notifications.Notifier;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestRunReportGenerator {

    public void generateFromNode(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (!(userObject instanceof TestRunDirectoryDto trDirDto)) return;

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                Path dirPath = trDirDto.getPath();
                String folderName = dirPath.getFileName().toString();
                File jsonFile = dirPath.resolve(folderName + ".json").toFile();

                if (!jsonFile.exists() || !jsonFile.isFile()) {
                    Notifier.error("Report Error", "JSON data file not found: " + jsonFile.getAbsolutePath());
                    return;
                }

                TestRunDto runData = Config.getMapper().readValue(jsonFile, TestRunDto.class);

                TestRunReport reportBuilder = new TestRunReport();
                String reportHtml = reportBuilder.build(runData);

                String cleanName = runData.getRunName().replace(".json", "");
                File reportFile = dirPath.resolve(cleanName + "_Report.html").toFile();
                Files.writeString(reportFile.toPath(), reportHtml);

                Notifier.infoWithOpenAndCopy(
                        "Report Generated",
                        "Saved successfully: " + reportFile.getName(),
                        reportFile
                );

            } catch (Exception e) {
                Notifier.error("Report Error", "Failed to generate report: " + e.getMessage());
            }
        });
    }
}