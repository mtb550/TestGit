package testGit.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import testGit.pojo.dto.TestCaseDto;
import testGit.pojo.dto.TestRunDto;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum RunEditorAttributes {

    DESCRIPTION("Description",
            true,
            true,
            item -> Optional.ofNullable(item.getTestCaseDetails()).map(TestCaseDto::getDescription).orElse("")
    ),

    EXPECTED_RESULT("Expected Result",
            true,
            true,
            item -> Optional.ofNullable(item.getTestCaseDetails()).map(TestCaseDto::getExpectedResult).orElse("")
    ),

    STEPS("Steps",
            true,
            true,
            item -> Optional.ofNullable(item.getTestCaseDetails())
                    .map(TestCaseDto::getSteps)
                    .map(steps -> String.join(", ", steps))
                    .orElse("")
    ),

    PRIORITY("Priority",
            true,
            true,
            item -> Optional.ofNullable(item.getTestCaseDetails())
                    .map(TestCaseDto::getPriority)
                    .map(Priority::getName)
                    .orElse("")
    ),

    GROUP("Group",
            true,
            true,
            item -> Optional.ofNullable(item.getTestCaseDetails())
                    .map(TestCaseDto::getGroup)
                    .map(groups -> groups.stream().map(Group::getName).collect(Collectors.joining(", ")))
                    .orElse("")
    ),

    ACTUAL_RESULT("Actual Result",
            true,
            true,
            item -> Optional.of(item.getActualResult()).orElse("")
    ),

    RUN_STATUS("Run Status",
            true,
            true,
            item -> Optional.of(item.getStatus()).map(Enum::name).orElse("")
    ),

    DURATION("Duration",
            true,
            true,
            item -> Optional.of(item.getDuration()).map(Object::toString).orElse("")
    );

    private final String name;
    private final boolean standardToolBarOption;
    private final boolean defaultToolBarSelected;

    private final Function<TestRunDto.TestRunItems, String> valueExtractor;

    public String getValue(final TestRunDto.TestRunItems runItem) {
        try {
            return valueExtractor.apply(runItem);
        } catch (Exception e) {
            return "Unknown";
        }
    }
}