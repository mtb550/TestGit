package org.testin.viewPanel.details.components;

import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.dto.TestCaseDto;

import java.awt.*;

public class ExpectedResult extends BaseDetails {
    @Override
    public int render(@NotNull JBPanel<?> panel, @NotNull GridBagConstraints gbc, @NotNull TestCaseDto dto, int currentRow) {
        return addRow(panel, gbc, "Expected Result:", dto.getExpectedResult(), currentRow);
    }
}