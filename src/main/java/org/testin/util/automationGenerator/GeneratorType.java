package org.testin.util.automationGenerator;

import lombok.Getter;

@Getter
public enum GeneratorType {
    CREATE_PROJECT(
            "Create Test Project",
            "Create Automation Test Project",
            new CreateTestProject()
    ),

    CREATE_TEST_SET(
            "Create Test Set",
            "Create Automation Test Set",
            new CreateTestSet()
    ),

    CREATE_TEST_SET_PACKAGE(
            "Create Test Set Package",
            "Create Automation Test Package",
            new CreateTestPackage()
    ),

    CREATE_TEST_CASE(
            "Create Test Case",
            "Create Automation Test Case",
            new CreateTestCase()
    ),

    UPDATE_TEST_CASE(
            "Update Test Case",
            "Update Automation Test Case",
            new UpdateTestCase()
    );

    private final String description;
    private final String tooltip;
    private final GeneratorAction action;

    GeneratorType(final String description, final String tooltip, final GeneratorAction action) {
        this.description = description;
        this.tooltip = tooltip;
        this.action = action;
    }
}