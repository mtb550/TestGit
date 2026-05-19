package org.testin.util.reports;

import org.jetbrains.annotations.NotNull;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.pojo.dto.TestRunDto;
import org.testin.util.Tools;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public final class HtmlGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generate(final @NotNull TestRunDto tr, final Map<UUID, TestCaseDto> detailsMap) {
        StringBuilder html = new StringBuilder();

        html.append("<html><head><style>")
                .append(".table-container { width: 100%; overflow-x: auto; border: 1px solid #ccc; margin-top: 10px; }")
                .append("table { border-collapse: collapse; font-size: 12px; font-family: sans-serif; width: max-content; }")
                .append("th { background-color: #f4f4f4; text-align: left; padding: 8px; border: 1px solid #ddd; }")
                .append("td { padding: 0; border: 1px solid #ddd; vertical-align: top; }")
                .append(".cell-content { padding: 8px; overflow-wrap: break-word; white-space: normal; }")
                .append("</style></head><body>");

        html.append("<h2>Test Run Report: ").append(tr.getRunName().replace(".json", "")).append("</h2>");
        html.append("<p><strong>Platform:</strong> ").append(tr.getPlatform()).append("</p>");
        html.append("<p><strong>Status:</strong> ").append(tr.getStatus().name()).append("</p>");

        html.append("<div class='table-container'>")
                .append("<table>")
                .append("<tr>")
                .append("<th>#</th>")
                .append("<th>ID</th>")
                .append("<th>Title</th>")
                .append("<th>Status</th>")
                .append("<th>Duration</th>")
                .append("<th>Expected Result</th>")
                .append("<th>Priority</th>")
                .append("<th>Module</th>")
                .append("<th>Groups</th>")
                .append("<th>Created By</th>")
                .append("<th>Updated By</th>")
                .append("<th>Created At</th>")
                .append("<th>Updated At</th>")
                .append("<th>Reference</th>")
                .append("<th>Steps</th>")
                .append("<th>FQCN</th>")
                .append("<th>Code</th>")
                .append("</tr>");

        if (!tr.getResults().isEmpty()) {
            AtomicInteger seq = new AtomicInteger(1);

            tr.getResults().forEach(result -> {
                UUID id = result.getTestCaseId();
                TestCaseDto d = detailsMap.get(id);

                String statusText = result.getStatus().name();
                String colorHex = "#" + result.getStatus().getHex();
                String duration = Tools.getInstance().getFormattedDuration(result.getDuration());

                String createdAt = d.getCreatedAt().format(DATE_FORMATTER);
                String updatedAt = d.getUpdatedAt().format(DATE_FORMATTER);

                String groups = d.getGroup().stream().map(Enum::name).collect(Collectors.joining("<br>"));
                String steps = String.join("<br>", d.getSteps());
                String fqcn = String.join("<br>", d.getFqcn());

                html.append("<tr>")
                        .append(wrap(String.valueOf(seq.getAndIncrement()), "40px"))
                        .append(wrap(id == null ? "" : id.toString(), "250px"))
                        .append(wrap(d.getDescription(), "500px"))
                        .append("<td style='color:").append(colorHex)
                        .append("; font-weight:bold;'><div class='cell-content' style='max-width:100px;'>")
                        .append(statusText).append("</div></td>")
                        .append(wrap(duration, "100px"))
                        .append(wrap(d.getExpectedResult(), "500px"))
                        .append(wrap(d.getPriority().name(), "80px"))
                        .append(wrap(d.getModule(), "150px"))
                        .append(wrap(groups, "150px"))
                        .append(wrap(d.getCreatedBy(), "150px"))
                        .append(wrap(d.getUpdatedBy(), "150px"))
                        .append(wrap(createdAt, "250px"))
                        .append(wrap(updatedAt, "250px"))
                        .append(wrap(d.getReference(), "150px"))
                        .append(wrap(steps, "300px"))
                        .append(wrap(fqcn, "250px"))
                        .append(wrap("<a href='#'>Navigate</a>", "80px"))
                        .append("</tr>");
            });
        } else {
            html.append("<tr><td colspan='17' style='text-align:center;'>No test results found.</td></tr>");
        }

        html.append("</table>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private String wrap(String content, String maxWidth) {
        return "<td><div class='cell-content' style='max-width:" + maxWidth + ";'>" + content + "</div></td>";
    }
}