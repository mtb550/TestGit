package testGit.util;

import testGit.pojo.dto.TestCaseDto;
import testGit.util.notifications.Notifier;

import java.util.*;

///  why dont make it in separate thread?
public class TestCaseSorter {
    public static SortResult sortTestCases(final List<TestCaseDto> unsortedList) {
        if (unsortedList == null || unsortedList.isEmpty()) {
            return new SortResult(new ArrayList<>(), new HashSet<>());
        }

        Map<UUID, TestCaseDto> idMap = new HashMap<>();
        TestCaseDto head = null;

        for (TestCaseDto tc : unsortedList) {
            idMap.put(tc.getId(), tc);
            if (tc.getIsHead() != null && tc.getIsHead()) {
                head = tc;
            }
        }

        List<TestCaseDto> sortedList = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        Set<UUID> unsortedIds = new HashSet<>();

        if (head == null) {
            Notifier.warn("Warning", "No Head found in test cases.");
            unsortedList.forEach(tc -> unsortedIds.add(tc.getId()));
            return new SortResult(unsortedList, unsortedIds);
        }

        TestCaseDto current = head;
        while (current != null && !visited.contains(current.getId())) {
            sortedList.add(current);
            visited.add(current.getId());

            UUID nextUuid = current.getNext();
            current = (nextUuid != null) ? idMap.get(nextUuid) : null;
        }

        if (sortedList.size() < unsortedList.size()) {
            for (TestCaseDto tc : unsortedList) {
                if (!visited.contains(tc.getId())) {
                    sortedList.add(tc);
                    unsortedIds.add(tc.getId());
                }
            }
        }

        return new SortResult(sortedList, unsortedIds);
    }

    public record SortResult(List<TestCaseDto> sortedList, Set<UUID> unsortedIds) {
    }
}