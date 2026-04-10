package testGit.util.reports;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.TestRunDto;

import java.io.ByteArrayOutputStream;

public final class PdfGenerator {

    public byte[] generate(final @NotNull TestRunDto tr) throws Exception {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            String cleanName = tr.getRunName().replace(".json", "");

            Paragraph header = new Paragraph("Test Run Report: " + cleanName)
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(header);

            document.add(new Paragraph("Platform: " + (tr.getPlatform() != null ? tr.getPlatform() : "N/A")));
            document.add(new Paragraph("Status: " + tr.getStatus()).setMarginBottom(20));

            Table table = new Table(UnitValue.createPercentArray(new float[]{40, 30, 30}))
                    .useAllAvailableWidth();

            table.addHeaderCell(createHeaderCell("Test Case ID", boldFont));
            table.addHeaderCell(createHeaderCell("Status", boldFont));
            table.addHeaderCell(createHeaderCell("Duration", boldFont));

            if (tr.getResults() != null) {
                tr.getResults().forEach(result -> {
                    table.addCell(new Cell().add(new Paragraph(result.getTestCaseId().toString())));

                    Cell statusCell = new Cell().add(new Paragraph(result.getStatus()));
                    if ("PASSED".equalsIgnoreCase(result.getStatus())) {
                        statusCell.setFontColor(ColorConstants.GREEN);
                    } else if ("FAILED".equalsIgnoreCase(result.getStatus())) {
                        statusCell.setFontColor(ColorConstants.RED);
                    }
                    table.addCell(statusCell);

                    String duration = result.getDuration() != null ? result.getDuration().toString() : "N/A";
                    table.addCell(new Cell().add(new Paragraph(duration)));
                });
            } else {
                Cell emptyCell = new Cell(1, 3).add(new Paragraph("No test results found."))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(emptyCell);
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        }
    }

    private Cell createHeaderCell(String text, PdfFont boldFont) {
        return new Cell()
                .add(new Paragraph(text).setFont(boldFont))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
    }
}