package testGit.viewPanel.details.components;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.dto.TestCaseDto;
import testGit.util.Tools;

import java.awt.*;

public class Title extends BaseDetails {

    private static final float TITLE_FONT_SIZE = 26.0f;

    private static final int INSETS_TOP = 20;
    private static final int INSETS_LEFT = 16;
    private static final int INSETS_BOTTOM = 0;
    private static final int INSETS_RIGHT = 16;

    @Override
    public int render(@NotNull final JBPanel<?> panel, @NotNull final GridBagConstraints gbc, @NotNull final TestCaseDto dto, final int currentRow) {
        final String titleText = Tools.format(dto.getTitle());
        final String htmlTitle = "<html><body style='width: 100%'>" + titleText + "</body></html>";

        JBLabel mainTitleLabel = new JBLabel(htmlTitle);

        mainTitleLabel.setFont(JBFont.label().deriveFont(Font.BOLD, TITLE_FONT_SIZE));

        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = JBUI.insets(INSETS_TOP, INSETS_LEFT, INSETS_BOTTOM, INSETS_RIGHT);

        panel.add(mainTitleLabel, gbc);

        return currentRow + 1;
    }
}