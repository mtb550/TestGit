package testGit.viewPanel.details.components;

import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.TestCaseDto;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Steps extends BaseDetails {
    private static final String LABEL_TEXT = "Steps:";
    private static final String EMPTY_PLACEHOLDER = "-";
    private static final String STEP_SEPARATOR = "\n";
    private static final int MINIMUM_VISIBLE_STEPS = 8;

    @Override
    public int render(@NotNull final JBPanel<?> panel, @NotNull final GridBagConstraints gbc, final @NotNull TestCaseDto dto, final int row) {
        return addRow(panel, gbc, LABEL_TEXT, formatSteps(dto.getSteps()), row);
    }

    private String formatSteps(List<String> steps) {
        List<String> formattedSteps = new ArrayList<>();

        if (steps != null && !steps.isEmpty()) {
            for (int i = 0; i < steps.size(); i++) {
                formattedSteps.add((i + 1) + "- " + steps.get(i));
            }
        } else {
            formattedSteps.add(EMPTY_PLACEHOLDER);
        }

        while (formattedSteps.size() < MINIMUM_VISIBLE_STEPS) {
            formattedSteps.add(" ");
        }

        return String.join(STEP_SEPARATOR, formattedSteps);
    }
}