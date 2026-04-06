package testGit.util.cache;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestCaseCache {
    private static final Set<String> titles = new HashSet<>();
    private static final Set<String> expectedResults = new HashSet<>();
    private static final Set<String> steps = new HashSet<>();

    public static Set<String> getTitles() {
        return Collections.unmodifiableSet(titles);
    }

    public static Set<String> getExpectedResults() {
        return Collections.unmodifiableSet(expectedResults);
    }

    public static Set<String> getSteps() {
        return Collections.unmodifiableSet(steps);
    }

    public static void addTitle(String t) {
        if (t != null && !t.trim().isEmpty()) titles.add(t.trim());
    }

    public static void addExpected(String e) {
        if (e != null && !e.trim().isEmpty()) expectedResults.add(e.trim());
    }

    public static void addStep(String s) {
        if (s != null && !s.trim().isEmpty()) steps.add(s.trim());
    }
}