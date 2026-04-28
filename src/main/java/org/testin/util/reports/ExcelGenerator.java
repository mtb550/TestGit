package org.testin.util.reports;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.TestStatus;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.pojo.dto.TestRunDto;
import org.testin.util.Tools;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.UUID;

public final class ExcelGenerator {

    public byte[] generate(final @NotNull TestRunDto tr, final Map<UUID, TestCaseDto> detailsMap) throws Exception {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            Workbook wb = new Workbook(os, "TestGit", "1.0");
            Worksheet ws = wb.newWorksheet("Test Run Report");

            ws.value(0, 0, "Test Run Report:");
            ws.style(0, 0).bold().fontSize(14).set();
            ws.value(0, 1, tr.getRunName().replace(".json", ""));

            ws.value(1, 0, "Platform:");
            ws.style(1, 0).bold().set();
            ws.value(1, 1, tr.getPlatform() != null ? tr.getPlatform() : "N/A");

            ws.value(2, 0, "Status:");
            ws.style(2, 0).bold().set();
            ws.value(2, 1, tr.getStatus() != null ? tr.getStatus().name() : "N/A");

            int row = 4;
            ws.value(row, 0, "Test Case ID");
            ws.value(row, 1, "Description");
            ws.value(row, 2, "Status");
            ws.value(row, 3, "Duration");
            ws.value(row, 4, "Expected Result");
            ws.value(row, 5, "Stacktrace");

            ws.range(row, 0, row, 5).style().bold().fillColor("E0E0E0").set();

            row++;
            if (tr.getResults() != null) {
                for (var result : tr.getResults()) {
                    UUID id = result.getTestCaseId();
                    ws.value(row, 0, id != null ? id.toString() : "N/A");

                    TestCaseDto details = (detailsMap != null) ? detailsMap.get(id) : null;
                    String title = details != null ? details.getDescription() : "N/A";
                    String expectedResult = details != null ? details.getExpectedResult() : "N/A";

                    ws.value(row, 1, title);

                    TestStatus statusEnum = result.getStatus();
                    ws.value(row, 2, statusEnum.name());
                    ws.style(row, 2).fontColor(statusEnum.getHex()).bold().set();

                    String formattedDuration = Tools.getFormattedDuration(result.getDuration());
                    ws.value(row, 3, formattedDuration != null ? formattedDuration : "N/A");

                    ws.value(row, 4, expectedResult);
                    ws.style(row, 4).wrapText(true).set();

                    ws.value(row, 5, result.getStacktrace());
                    ws.style(row, 5).wrapText(true).set();

                    row++;
                }
            } else {
                ws.value(row, 0, "No test results found.");
            }

            ws.width(0, 40); // ID
            ws.width(1, 30); // Title
            ws.width(2, 15); // Status
            ws.width(3, 15); // Duration
            ws.width(4, 40); // Expected Result
            ws.width(5, 60); // Stacktrace

            wb.finish();

            return os.toByteArray();
        }
    }
}