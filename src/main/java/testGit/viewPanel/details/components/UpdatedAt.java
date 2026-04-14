package testGit.viewPanel.details.components;

import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.TestCaseDto;

import java.awt.*;

public class UpdatedAt extends BaseDetails {

    private static final String LABEL_TEXT = "Updated At:";

    @Override
    public int render(@NotNull final JBPanel<?> panel, @NotNull final GridBagConstraints gbc, @NotNull final TestCaseDto dto, final int currentRow) {
        final String date = dto.getFormattedUpdatedAt();
        return addRow(panel, gbc, LABEL_TEXT, date, currentRow);
    }
}