package testGit.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import testGit.editorPanel.Shared;
import testGit.pojo.dto.TestCaseDto;
import testGit.pojo.dto.TestRunDto;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum RunEditorAttributes {

    DESCRIPTION("Description",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getTestCaseDetails).map(TestCaseDto::getDescription).orElse(""),
            null
    ),

    EXPECTED_RESULT("Expected Result",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getTestCaseDetails).map(TestCaseDto::getExpectedResult).orElse(""),
            null
    ),

    STEPS("Steps",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getTestCaseDetails)
                    .map(TestCaseDto::getSteps)
                    .map(steps -> String.join(", ", steps))
                    .orElse(""),
            null
    ),

    PRIORITY("Priority",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getTestCaseDetails)
                    .map(TestCaseDto::getPriority)
                    .map(Priority::getName)
                    .orElse(""),
            tc -> Optional.ofNullable(tc).map(TestCaseDto::getPriority)
                    .map(p -> List.<JComponent>of(Shared.createPriorityBadge(tc)))
                    .orElse(Collections.emptyList())
    ),

    GROUP("Group",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getTestCaseDetails)
                    .map(TestCaseDto::getGroup)
                    .map(groups -> groups.stream().map(Group::getName).collect(Collectors.joining(", ")))
                    .orElse(""),
            tc -> Optional.ofNullable(tc).map(TestCaseDto::getGroup)
                    .map(groups -> groups.stream()
                            .map(Shared::createGroupBadge)
                            .collect(Collectors.<JComponent>toList()))
                    .orElse(Collections.emptyList())
    ),

    ACTUAL_RESULT("Actual Result",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getActualResult).orElse(""),
            null
    ),

    RUN_STATUS("Run Status",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getStatus).map(Enum::name).orElse(""),
            null
    ),

    DURATION("Duration",
            true,
            true,
            item -> Optional.ofNullable(item).map(TestRunDto.TestRunItems::getDuration).map(Object::toString).orElse(""),
            null
    );

    private final String name;
    private final boolean standardToolBarOption;
    private final boolean defaultToolBarSelected;
    private final Function<TestRunDto.TestRunItems, String> valueExtractor;
    private final Function<TestCaseDto, List<JComponent>> badgeExtractor;

    public String getValue(final TestRunDto.TestRunItems runItem) {
        try {
            return valueExtractor.apply(runItem);
        } catch (Exception e) {
            return "Unknown";
        }
    }
}