package com.example.demo;

import java.util.List;

public class DB {
    public static List<Project> loadProjects() {
        return List.of(
                new Project("Project A", List.of(
                        new Feature(1, "Login"),
                        new Feature(2, "Logout")
                ))
        );
    }

    public static List<TestCase> loadTestCases(int featureId) {
        return List.of(
                new TestCase(1, "Valid Login", "User is logged in", "Enter credentials → Click Login", "High"),
                new TestCase(2, "Invalid Login", "Error message shown", "Enter wrong password → Click Login", "Medium")
        );
    }

    public static List<TestCaseHistory> loadTestCaseHistory(int testCaseId) {
        return List.of(
                new TestCaseHistory("2025-03-28 10:30", "Priority updated to High"),
                new TestCaseHistory("2025-03-26 08:15", "Step 2 added"),
                new TestCaseHistory("2025-03-20 15:00", "Test case created")
        );
    }
}
