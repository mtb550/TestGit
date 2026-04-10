package testGit.util.reports;

import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.TestRunDto;

public final class TestRunReport {

    public String build(final @NotNull TestRunDto tr) {
        StringBuilder html = new StringBuilder();

        html.append("<html><head><style>table {width:100%; border-collapse:collapse;} th,td {border:1px solid #ddd; padding:8px;} th {background-color: #f4f4f4;}</style></head><body>");
        html.append("<h2>Test Run Report: ").append(tr.getRunName().replace(".json", "")).append("</h2>");
        html.append("<p><strong>Platform:</strong> ").append(tr.getPlatform() != null ? tr.getPlatform() : "N/A").append("</p>");
        html.append("<p><strong>Status:</strong> ").append(tr.getStatus()).append("</p>");

        html.append("<table><tr><th>Test Case ID</th><th>Status</th><th>Duration</th></tr>");

        if (tr.getResults() != null) {
            tr.getResults().forEach(result ->
                    html.append("<tr>")
                            .append("<td>").append(result.getTestCaseId()).append("</td>")
                            .append("<td>").append(result.getStatus()).append("</td>")
                            .append("<td>").append(result.getDuration() != null ? result.getDuration() : "N/A").append("</td>")
                            .append("</tr>"));
        }

        html.append("</table></body></html>");

        return html.toString();
    }
}