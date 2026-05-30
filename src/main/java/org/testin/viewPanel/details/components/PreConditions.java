package org.testin.viewPanel.details.components;

import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.TestEditorAttributes;
import org.testin.pojo.dto.TestCaseDto;
import org.testin.util.Tools;

import java.awt.*;

public class PreConditions extends BaseDetails {
    @Override
    public int render(@NotNull final JBPanel<?> panel, @NotNull final GridBagConstraints gbc, @NotNull final TestCaseDto dto, final int currentRow) {
        return addRow(panel, gbc, TestEditorAttributes.PRE_CONDITIONS.getName2(), Tools.getInstance().format(dto.getPreConditions()), currentRow);
    }
}
