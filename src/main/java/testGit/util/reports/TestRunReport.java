package testGit.util.reports;

import com.intellij.openapi.application.ApplicationManager;
import testGit.pojo.Config;
import testGit.pojo.dto.TestRunDto;
import testGit.pojo.dto.dirs.TestRunDirectoryDto;
import testGit.util.notifications.Notifier;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestRunReport {
    private final TestRunDirectoryDto tr;

    public TestRunReport(final TestRunDirectoryDto tr) {
        this.tr = tr;
    }

    public TestRunReport build() {
        return this;
    }

    public void asHtml() {
        processAndSave("HTML", ".html");
    }

    public void asPdf() {
        processAndSave("PDF", ".pdf");
    }

    public void asExcel() {
        processAndSave("EXCEL", ".xlsx");
    }

    private void processAndSave(String format, String extension) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                Path dirPath = tr.getPath();
                String folderName = dirPath.getFileName().toString();
                File jsonFile = dirPath.resolve(folderName + ".json").toFile();

                if (!jsonFile.exists() || !jsonFile.isFile()) {
                    Notifier.error("Report Error", "JSON data file not found: " + jsonFile.getAbsolutePath());
                    return;
                }

                TestRunDto runData = Config.getMapper().readValue(jsonFile, TestRunDto.class);
                byte[] fileBytes;

                switch (format) {
                    case "HTML" -> {
                        String reportHtml = new HtmlGenerator().generate(runData);
                        fileBytes = reportHtml.getBytes(StandardCharsets.UTF_8);
                    }

                    case "PDF" -> fileBytes = new PdfGenerator().generate(runData);

                    case "EXCEL" -> fileBytes = new ExcelGenerator().generate(runData);

                    case null, default -> throw new UnsupportedOperationException("Unknown format: " + format);
                }

                String cleanName = runData.getRunName().replace(".json", "");
                File reportFile = dirPath.resolve(cleanName + "_Report" + extension).toFile();

                Files.write(reportFile.toPath(), fileBytes);

                Notifier.infoWithOpenAndCopy(
                        format + " Report Generated",
                        "Saved successfully: " + reportFile.getName(),
                        reportFile
                );

            } catch (Exception e) {
                Notifier.error("Report Error", "Failed to generate " + format + " report: " + e.getMessage());
            }
        });
    }
}