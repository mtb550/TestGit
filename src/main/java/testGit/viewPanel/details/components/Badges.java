package testGit.viewPanel.details.components;

import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import testGit.editorPanel.Shared;
import testGit.pojo.Groups;
import testGit.pojo.dto.TestCaseDto;

import javax.swing.*;
import java.awt.*;

public class Badges extends BaseDetails {

    private static final int FLOW_GAP = 6;
    private static final int EMPTY_STRUT_HEIGHT = 20;
    private static final int INSETS_TOP = 8;
    private static final int INSETS_LEFT = 16;
    private static final int INSETS_BOTTOM = 16;
    private static final int INSETS_RIGHT = 16;

    @Override
    public int render(@NotNull final JBPanel<?> panel, @NotNull final GridBagConstraints gbc, @NotNull final TestCaseDto dto, final int currentRow) {
        JPanel badgesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, JBUI.scale(FLOW_GAP), 0));
        badgesPanel.setOpaque(false);

        boolean hasContent = false;
        if (dto.getPriority() != null) {
            badgesPanel.add(Shared.createPriorityBadge(dto));
            hasContent = true;
        }

        if (dto.getGroups() != null && !dto.getGroups().isEmpty()) {
            for (Groups groups : dto.getGroups()) {
                if (groups != null) {
                    badgesPanel.add(Shared.createGroupBadge(groups));
                    hasContent = true;
                }
            }
        }

        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        if (!hasContent) {
            badgesPanel.add(Box.createVerticalStrut(JBUI.scale(EMPTY_STRUT_HEIGHT)));
        }

        gbc.insets = JBUI.insets(INSETS_TOP, INSETS_LEFT, INSETS_BOTTOM, INSETS_RIGHT);
        panel.add(badgesPanel, gbc);

        return currentRow + 1;
    }
}