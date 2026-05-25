package org.testin.util.reports;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ide.CopyPasteManager;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.Config;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.pojo.dto.TestRunDto;
import org.testin.pojo.dto.dirs.TestRunDirectoryDto;
import org.testin.util.notifications.Notifier;

import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

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
                    Notifier.getInstance().error("Report Error", "JSON data file not found: " + jsonFile.getAbsolutePath());
                    return;
                }

                TestRunDto runData = Config.getMapper().readValue(jsonFile, TestRunDto.class);

                Map<UUID, TestCaseDto> detailsMap = fetchTestCaseDetails(runData);

                byte[] fileBytes;

                switch (format) {
                    case "HTML" -> {
                        ///  todo, to be implemented, put report file types in enum class
                        String reportHtml = new HtmlGenerator().generate(runData, detailsMap);
                        fileBytes = reportHtml.getBytes(StandardCharsets.UTF_8);
                    }

                    case "PDF" -> fileBytes = new PdfGenerator().generate(runData, detailsMap);

                    case "EXCEL" -> fileBytes = new ExcelGenerator().generate(runData, detailsMap);

                    case null, default -> throw new UnsupportedOperationException("Unknown format: " + format);
                }

                String cleanName = runData.getRunName().replace(".json", "");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
                String timestamp = LocalDateTime.now().format(formatter);

                File reportFile = dirPath.resolve(cleanName + "_Report_" + timestamp + extension).toFile();

                Files.write(reportFile.toPath(), fileBytes);

                NotificationAction openAction = NotificationAction.createSimple("Open report", () ->
                        BrowserUtil.browse(reportFile.toURI().toString())
                );

                NotificationAction copyAction = new NotificationAction("Copy path") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                        CopyPasteManager.getInstance().setContents(new StringSelection(reportFile.getAbsolutePath()));
                    }
                };
                copyAction.getTemplatePresentation().setIcon(AllIcons.Actions.Copy);

                Notifier.getInstance().infoWithActions(
                        format + " Report Generated",
                        "Saved successfully: " + reportFile.getName(),
                        openAction,
                        copyAction
                );

            } catch (Exception e) {
                Notifier.getInstance().error("Report Error", "Failed to generate " + format + " report: " + e.getMessage());
            }
        });
    }

    private Map<UUID, TestCaseDto> fetchTestCaseDetails(TestRunDto tr) {
        Map<UUID, TestCaseDto> detailsMap = new ConcurrentHashMap<>();

        if (tr.getTestCase().isEmpty()) {
            return detailsMap;
        }

        for (TestRunDto.TestCase tcPathObj : tr.getTestCase()) {
            Path dirPath = tcPathObj.getPath();
            List<UUID> targetIds = tcPathObj.getUuid();

            if (dirPath == null || !Files.exists(dirPath) || targetIds.isEmpty()) {
                continue;
            }

            Set<UUID> idsToFind = new HashSet<>(targetIds);

            try (Stream<Path> paths = Files.list(dirPath)) {
                paths.filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".json"))
                        .parallel()
                        .forEach(p -> {
                            try {
                                TestCaseDto tc = Config.getMapper().readValue(p.toFile(), TestCaseDto.class);
                                if (idsToFind.contains(tc.getId())) {
                                    detailsMap.put(tc.getId(), tc);
                                }
                            } catch (Exception ignored) {
                            }
                        });
            } catch (Exception e) {
                System.err.println("Failed to load details from path " + dirPath + ": " + e.getMessage());
            }
        }
        return detailsMap;
    }

    public void asJson() {
        // TODO: to be implemented
    }

    public void asXml() {
        // TODO: to be implemented
    }

}