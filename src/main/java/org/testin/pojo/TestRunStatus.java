package org.testin.pojo;

import lombok.Getter;

@Getter
public enum TestRunStatus {
    CREATED(
            "Created"
    ),

    IN_PROGRESS(
            "In Progress"
    ),

    COMPLETED(
            "Completed"
    ),

    ASSIGNED(
            "Assigned to Alzamil" //todo, later, use xml to add tester's name dynamic
    ),

    CLOSED(
            "Closed"
    );

    private final String label;

    TestRunStatus(final String label) {
        this.label = label;
    }

}

