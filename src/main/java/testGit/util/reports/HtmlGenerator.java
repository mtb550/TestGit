package testGit.util.reports;

import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestStatus;
import testGit.pojo.dto.TestCaseDto;
import testGit.pojo.dto.TestRunDto;
import testGit.util.Tools;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class HtmlGenerator {

    public String generate(final @NotNull TestRunDto tr, final Map<UUID, TestCaseDto> detailsMap) {
        StringBuilder html = new StringBuilder();

        html.append("<html><head><style>table {width:100%; border-collapse:collapse;} th,td {border:1px solid #ddd; padding:8px;} th {background-color: #f4f4f4;}</style></head><body>");
        html.append("<h2>Test Run Report: ").append(tr.getRunName().replace(".json", "")).append("</h2>");
        html.append("<p><strong>Platform:</strong> ").append(tr.getPlatform() != null ? tr.getPlatform() : "N/A").append("</p>");
        html.append("<p><strong>Status:</strong> ").append(tr.getStatus() != null ? tr.getStatus().name() : "N/A").append("</p>");
        html.append("<table><tr><th>#</th><th>Test Case ID</th><th>Title</th><th>Status</th><th>Duration</th><th>Expected Result</th></tr>");

        if (tr.getResults() != null && !tr.getResults().isEmpty()) {
            AtomicInteger seq = new AtomicInteger(1);

            tr.getResults().forEach(result -> {
                UUID id = result.getTestCaseId();

                TestCaseDto details = (detailsMap != null) ? detailsMap.get(id) : null;
                String title = details != null ? details.getTitle() : "N/A";
                String expected = details != null ? details.getExpected() : "N/A";

                TestStatus statusEnum = result.getStatus();
                String statusText = statusEnum.name();
                String colorHex = "#" + statusEnum.getHex();

                String duration = Tools.getFormattedDuration(result.getDuration());

                html.append("<tr>")
                        .append("<td>").append(seq.getAndIncrement()).append("</td>")
                        .append("<td>").append(id != null ? id.toString() : "N/A").append("</td>")
                        .append("<td>").append(title).append("</td>")
                        .append("<td style='color:").append(colorHex).append("; font-weight:bold;'>").append(statusText).append("</td>")
                        .append("<td>").append(duration != null ? duration : "N/A").append("</td>")
                        .append("<td>").append(expected).append("</td>")
                        .append("</tr>");
            });
        } else {
            html.append("<tr><td colspan='6' style='text-align:center;'>No test results found.</td></tr>");
        }

        html.append("</table></body></html>");

        return html.toString();
    }
}