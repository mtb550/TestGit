package testGit.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.TestRunStatus;
import testGit.pojo.TestStatus;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
public class TestRunDto {
    private String runName;

    private String buildNumber;

    private String platform;

    private String language;

    private String browser;

    private String deviceType;

    private TestRunStatus status;

    private LocalDateTime createdAt;

    private List<TestCase> testCase;

    private List<TestRunItems> results;

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class TestCase {
        private Path path;

        private List<UUID> uuid;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class TestRunItems {
        private UUID testCaseId;

        private String project;

        @NotNull
        private TestStatus status;

        private String actualResult;

        private Duration duration;

        private String executedBy;

        private LocalDateTime executedAt;

        private String stacktrace;

    }
}