package com.example.demo;

public class TestCase {
    private final String title;
    private final String expectedResult;
    private final String steps;
    private final String priority;
    private final int id;

    public TestCase(int id, String title, String expectedResult, String steps, String priority) {
        this.id = id;
        this.title = title;
        this.expectedResult = expectedResult;
        this.steps = steps;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public String getSteps() {
        return steps;
    }

    public String getPriority() {
        return priority;
    }
}
