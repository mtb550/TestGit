package com.example.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DB {
    public static List<Project> loadProjects() {
        List<TestCase> loginTests = Arrays.asList(
                new TestCase("login-01", "Login with valid credentials", "Dashboard shown", "Enter username and password", "High", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-02", "Login with invalid password", "Error message displayed", "Enter wrong password", "Medium", "test.LoginTest", List.of(GroupType.Sanity)),
                new TestCase("login-03", "Login with non-existent username", "Error message displayed", "Enter an unregistered username", "Medium", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-04", "Login with empty username", "Error message displayed", "Leave username blank", "Medium", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-05", "Login with empty password", "Error message displayed", "Leave password blank", "Medium", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-06", "Login with SQL injection attempt", "Error message displayed", "Enter SQL payload in username field", "High", "test.LoginTest", List.of(GroupType.Security)),
                new TestCase("login-07", "Login with cross-site scripting (XSS)", "Error message displayed", "Enter script tag in username field", "High", "test.LoginTest", List.of(GroupType.Security)),
                new TestCase("login-08", "Login with trailing spaces in username", "Dashboard shown", "Append spaces after valid username", "Low", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-09", "Login with case sensitivity check", "Dashboard shown", "Enter username in different case", "Low", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-10", "Login after password reset", "Dashboard shown", "Reset password then login", "High", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-11", "Login with multiple failed attempts", "Account locked", "Enter wrong password several times", "High", "test.LoginTest", List.of(GroupType.Security)),
                new TestCase("login-12", "Login using browser autofill", "Dashboard shown", "Use browser autofill for credentials", "Low", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-13", "Login with 'Remember Me' enabled", "Dashboard shown", "Select 'Remember Me' and login", "Low", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-14", "Login on mobile browser", "Dashboard shown", "Use mobile browser to login", "Low", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-15", "Login on desktop browser", "Dashboard shown", "Use desktop browser to login", "Low", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-16", "Login with VPN connection", "Dashboard shown", "Connect via VPN and login", "Medium", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-17", "Login with cookies disabled", "Error message displayed", "Disable cookies and attempt login", "High", "test.LoginTest", List.of(GroupType.Security)),
                new TestCase("login-18", "Login with session timeout", "Login screen shown", "Wait for session timeout then re-login", "Medium", "test.LoginTest", List.of(GroupType.Regression)),
                new TestCase("login-19", "Login with CAPTCHA challenge", "CAPTCHA prompt shown", "Trigger CAPTCHA by multiple logins", "High", "test.LoginTest", List.of(GroupType.Security)),
                new TestCase("login-20", "Login with two-factor authentication", "2FA prompt displayed", "Enter username and password, then 2FA code", "High", "test.LoginTest", List.of(GroupType.Regression))
        );


        List<TestCase> logoutTests = Arrays.asList(
                new TestCase("logout-01", "Logout from profile page", "Redirected to login", "Click logout", "Low", "test.LogoutTest.test5", List.of(GroupType.Smoke))
        );

        Feature loginFeature = new Feature("Login", loginTests);
        Feature logoutFeature = new Feature("Logout", logoutTests);

        Project project = new Project("Project A", Arrays.asList(loginFeature, logoutFeature));

        return new ArrayList<>(List.of(project));
    }

    public static List<TestCaseHistory> loadTestCaseHistory() {
        List<TestCaseHistory> history = new ArrayList<>();
        history.add(new TestCaseHistory("2024-03-01", "Created test case"));
        history.add(new TestCaseHistory("2024-03-15", "Updated expected result"));
        return history;
    }

    public static Feature getFeature(String projectName, String featureName) {
        return loadProjects().stream()
                .filter(p -> p.getName().equals(projectName))
                .flatMap(p -> p.getFeatures().stream())
                .filter(f -> f.getName().equals(featureName))
                .findFirst()
                .orElse(null);
    }

}
