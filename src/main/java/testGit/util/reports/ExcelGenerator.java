package testGit.util.reports;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.TestRunDto;

import java.io.ByteArrayOutputStream;

public final class ExcelGenerator {

    public byte[] generate(final @NotNull TestRunDto tr) throws Exception {
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
            ws.value(2, 1, tr.getStatus() != null ? tr.getStatus().toString() : "N/A");

            int row = 4;
            ws.value(row, 0, "Test Case ID");
            ws.value(row, 1, "Status");
            ws.value(row, 2, "Duration");
            ws.value(row, 3, "Stacktrace");

            ws.range(row, 0, row, 3).style().bold().fillColor("E0E0E0").set();

            row++;
            if (tr.getResults() != null) {
                for (var result : tr.getResults()) {
                    ws.value(row, 0, result.getTestCaseId().toString());

                    String status = result.getStatus() != null ? result.getStatus() : "N/A";
                    ws.value(row, 1, status);

                    ///  to be implemented, store colors in enum class to retrieve it wothout if statements
                    if ("PASSED".equalsIgnoreCase(status))
                        ws.style(row, 1).fontColor("008000").bold().set();

                    else if ("FAILED".equalsIgnoreCase(status))
                        ws.style(row, 1).fontColor("FF0000").bold().set();

                    else if ("BLOCKED".equalsIgnoreCase(status))
                        ws.style(row, 1).fontColor("FFA500").bold().set();


                    ws.value(row, 2, result.getDuration() != null ? result.getDuration().toString() : "N/A");

                    ws.value(row, 3, result.getStacktrace());
                    ws.style(row, 3).wrapText(true).set();

                    row++;
                }
            } else
                ws.value(row, 0, "No test results found.");

            ws.width(0, 40);
            ws.width(1, 15);
            ws.width(2, 15);
            ws.width(3, 80);

            wb.finish();

            return os.toByteArray();
        }
    }
}