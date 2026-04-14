package testGit.editorPanel.toolBar;

import com.intellij.icons.AllIcons;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.SearchTextField;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import testGit.util.IconManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

///  TODO: use interface that implement any tool bar button.
public class ToolBar extends JBPanel<ToolBar> {
    private final JButton refreshBtn;
    private final JButton detailsBtn;
    private final JButton filterBtn;
    @Getter
    private final SearchTextField searchField = new SearchTextField();
    private final ToolBarCallback callbacks;
    @Getter
    private final ToolBarSettings settings;

    public ToolBar(final ToolBarCallback callbacks) {
        super(new GridBagLayout());
        this.callbacks = callbacks;
        this.settings = new ToolBarSettings();

        setBackground(JBUI.CurrentTheme.EditorTabs.background());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;

        refreshBtn = createToolbarButton("Refresh", AllIcons.Actions.Refresh);
        refreshBtn.addActionListener(e -> callbacks.onRefresh());
        add(refreshBtn, gbc);

        gbc.gridx++;

        detailsBtn = createToolbarButton("Details", AllIcons.Actions.PreviewDetailsVertically);
        detailsBtn.addActionListener(e -> FilterPopupBuilder.detailsPopup(detailsBtn, settings.getSelectedDetails(), v -> {
            settings.save();
            callbacks.onDetailsChanged();
        }));
        add(detailsBtn, gbc);

        gbc.gridx++;

        filterBtn = createToolbarButton("Filter", AllIcons.General.Filter);
        filterBtn.addActionListener(e -> FilterPopupBuilder.filterPopup(filterBtn, settings.getSelectedPriority(), settings.getSelectedGroup(), this::resetFilters, v -> {
                    updateFilterBtnState();
                    callbacks.onFilterChanged();
                }
        ));
        add(filterBtn, gbc);

        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        searchField.setOpaque(false);
        searchField.getTextEditor().setOpaque(false);
        searchField.getTextEditor().setBackground(JBUI.CurrentTheme.EditorTabs.background());
        searchField.addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(final @NotNull DocumentEvent e) {
                callbacks.onFilterChanged();
            }
        });
        add(searchField, gbc);

        updateFilterBtnState();
    }

    private void updateFilterBtnState() {
        final int activeFiltersCount = settings.getSelectedPriority().size() + settings.getSelectedGroup().size();
        if (activeFiltersCount == 0) {
            filterBtn.setText(null);
            filterBtn.setToolTipText("Filter");
            filterBtn.setForeground(JBColor.foreground());

        } else {
            filterBtn.setText("(" + activeFiltersCount + ")");
            filterBtn.setToolTipText("Filter [" + activeFiltersCount + " active]");
            filterBtn.setForeground(JBUI.CurrentTheme.Link.Foreground.ENABLED);
        }
    }

    public String getSearchQuery() {
        return searchField.getText().trim().toLowerCase();
    }

    public void resetFilters() {
        settings.resetFilters();
        searchField.setText(null);
        updateFilterBtnState();
        callbacks.onFilterChanged();
    }

    private JButton createToolbarButton(final String tooltip, final Icon icon) {
        final JButton btn = new JButton(null, icon);
        btn.setToolTipText(tooltip);
        btn.setFocusable(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        final Icon zoomedIcon = IconManager.zoomStandardIcon(icon, btn);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                btn.setContentAreaFilled(true);
                btn.setBackground(JBUI.CurrentTheme.ActionButton.hoverBackground());
                btn.setIcon(zoomedIcon);
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                btn.setContentAreaFilled(false);
                btn.setIcon(icon);
            }
        });
        return btn;
    }
}