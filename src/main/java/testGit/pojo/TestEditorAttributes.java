package testGit.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import testGit.editorPanel.Shared;
import testGit.pojo.dto.TestCaseDto;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
/// TODO: add order, then add it toolbar details (select by the order number) & add it to edit menu.
/// TODO: add all to edit menu: auto ref, business ref..etc
/// TODO: also, map all to view panel dynamically.
/// TODO: may you need to unify enum map to map all with one source of truth
@AllArgsConstructor
public enum TestEditorAttributes {

    ID("ID",
            true,
            false,
            tc -> String.valueOf(tc.getId()),
            null
    ),

    /// TODO:: added to tool bar details, to be shown but disabled
    DESCRIPTION("Description",
            false,
            false,
            TestCaseDto::getDescription,
            null
    ),

    EXPECTED_RESULT("Expected Result",
            true,
            true,
            TestCaseDto::getExpectedResult,
            null
    ),

    STEPS("Steps",
            true,
            true,
            tc -> Optional.of(tc.getSteps()).map(Object::toString).orElse(""),
            null
    ),

    PRIORITY("Priority",
            true,
            true,
            tc -> Optional.of(tc.getPriority()).map(Priority::getName).orElse(""),
            tc -> Optional.of(tc.getPriority())
                    .map(p -> List.<JComponent>of(Shared.createPriorityBadge(tc)))
                    .orElse(Collections.emptyList())
    ),

    FCQN("FCQN",
            true,
            false,
            TestCaseDto::getFqcn,
            null
    ),

    REFERRENCE("Referrence",
            true,
            false,
            TestCaseDto::getReference,
            null
    ),

    GROUP("Group",
            true,
            true,
            tc -> Optional.of(tc.getGroup()).map(groups -> groups.stream().map(Group::getName).collect(Collectors.joining(", "))).orElse(""),
            tc -> Optional.of(tc.getGroup())
                    .map(groups -> groups.stream()
                            .map(Shared::createGroupBadge)
                            .collect(Collectors.<JComponent>toList()))
                    .orElse(Collections.emptyList())
    ),

    ///  TODO:: ORDER to be added to show or hide sequence numbers in editors

    CREATE_BY("Created By",
            true,
            false,
            tc -> null,
            null
    ),

    UPDATE_BY("Updated By",
            true,
            false,
            tc -> null,
            null
    ),

    CREATE_AT("Created At",
            true,
            false,
            tc -> null,
            null
    ),

    UPDATE_AT("Updated At",
            true,
            false,
            tc -> null,
            null
    ),

    MODULE("Module",
            true,
            false,
            TestCaseDto::getModule,
            null
    ),

    STATUS("Status",
            true,
            false,
            tc -> null,
            null
    );

    private final String name;
    private final boolean standardToolBarOption;
    private final boolean defaultToolBarSelected;
    private final Function<TestCaseDto, String> valueExtractor;
    private final Function<TestCaseDto, List<JComponent>> badgeExtractor;

    public String getValue(final TestCaseDto tc) {
        try {
            return valueExtractor.apply(tc);
        } catch (Exception e) {
            return "Unknown";
        }
    }
}