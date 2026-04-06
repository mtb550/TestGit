package testGit.ui.createTestCase;

import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import lombok.Getter;
import testGit.pojo.dto.TestCaseDto;
import testGit.util.KeyboardSet;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Set;

public class TitleSection implements CreateTestCaseSection {
    @Getter
    private final ExtendableTextField titleField;
    private final JPanel wrapper;
    Font fieldFont = JBFont.regular().deriveFont(JBUI.Fonts.label().getSize2D() + 6f);
    private boolean isError = false;

    public TitleSection() {
        this.titleField = new ExtendableTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && hasFocus()) {
                    try {
                        Rectangle2D r = modelToView2D(0);
                        if (r != null) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(isError ? JBColor.RED : UIUtil.getContextHelpForeground());
                            g2.setFont(getFont());
                            FontMetrics fm = g2.getFontMetrics();

                            int x = (int) r.getX() + JBUI.scale(1);
                            int y = (int) r.getY() + fm.getAscent() - JBUI.scale(1);

                            g2.drawString(getEmptyText().getText(), x, y);
                            g2.dispose();
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        };

        this.titleField.setFont(fieldFont);
        this.titleField.getEmptyText().setFont(fieldFont);
        this.titleField.getEmptyText().setText(CreateField.TITLE.getLabel());
        this.titleField.setBorder(JBUI.Borders.empty(10));

        this.wrapper = new JPanel(new BorderLayout());
        this.wrapper.setOpaque(false);
        this.wrapper.add(createIconPanel(CreateField.TITLE.getIcon()), BorderLayout.WEST);
        this.wrapper.add(this.titleField, BorderLayout.CENTER);
        this.wrapper.setBorder(JBUI.Borders.emptyTop(8));
    }

    public void setError(boolean error) {
        this.isError = error;
        titleField.getEmptyText().clear();

        if (error) {
            titleField.getEmptyText().appendText(CreateField.TITLE.getLabel(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED));
            titleField.requestFocus();
        } else
            titleField.getEmptyText().appendText(CreateField.TITLE.getLabel());

        titleField.repaint();
    }

    @Override
    public JPanel getWrapper() {
        return wrapper;
    }

    @Override
    public void showSection(JPanel contentPanel) {
        if (wrapper.getParent() == null)
            contentPanel.add(wrapper);
        titleField.requestFocus();
    }

    @Override
    public void applyTo(TestCaseDto dto) {
        if (wrapper.getParent() != null && titleField.isEditable())
            dto.setTitle(titleField.getText().trim());
    }

    @Override
    public void setupShortcut(final JComponent mainPanel, final JPanel slot, final CreateTestCaseBase base, final CreateTestCaseBase.UIAction repackAction, final Set<String> uniqueStepsCache) {
        base.registerShortcut(mainPanel, KeyboardSet.CreateTestCaseTitle.getShortcut(), () -> {
            showSection(slot);
            repackAction.execute();
        });
    }

    @Override
    public JComponent getFocusComponent() {
        return titleField;
    }

    @Override
    public void setEditable(final boolean editable) {
        titleField.setEditable(editable);
        titleField.setEnabled(editable);
        if (!editable)
            titleField.setForeground(UIUtil.getContextHelpForeground());
        else
            titleField.setForeground(UIUtil.getTextFieldForeground());
    }

    @Override
    public void fillData(final TestCaseDto dto, final CreateTestCaseBase.UIAction repackAction, final Set<String> uniqueStepsCache) {
        if (dto.getTitle() != null) {
            titleField.setText(dto.getTitle());
        }
    }
}