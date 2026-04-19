package testGit.editorPanel.testCaseEditor;

import com.intellij.ui.JBColor;
import testGit.editorPanel.BaseCard;
import testGit.editorPanel.Shared;
import testGit.pojo.TestEditorAttributes;
import testGit.pojo.dto.TestCaseDto;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TestCard extends BaseCard {
    private final List<JComponent> badges = new ArrayList<>();
    private final Map<String, String> details = new LinkedHashMap<>();

    public TestCard() {
        super();
    }

    public void updateData(final int index, final TestCaseDto tc, final Set<?> activeDetails, final boolean isUnsorted) {
        badges.clear();
        details.clear();

        System.out.println("render: " + tc.getDescription());
        System.out.println("activeDetails: " + activeDetails);
        System.out.println("----------\n\n");

        Arrays.stream(TestEditorAttributes.values())
                .filter(activeDetails::contains)
                .forEach(attr -> attr.applyToUI(tc, badges, details));

        if (isUnsorted) {
            badges.add(new Shared.RoundedBadge("Unsorted", new JBColor(new Color(255, 100, 100), new Color(130, 50, 50))));
        }

        updateUI(index, TestEditorAttributes.DESCRIPTION.getValueExtractor().apply(tc), badges, details);
    }
}