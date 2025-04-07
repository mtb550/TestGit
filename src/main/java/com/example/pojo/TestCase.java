package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class TestCase {

    private String id;

    private String title;

    private String expectedResult;

    private String steps;

    private String priority;

    private String automationRef;

    public Group group;

}
