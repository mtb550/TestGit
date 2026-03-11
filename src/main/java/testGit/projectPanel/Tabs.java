package testGit.projectPanel;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBFont;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@Getter
public class Tabs {
    private final JBPanel<?> mainPanel;
    private final CardLayout cardLayout;
    private final JBPanel<?> contentPanel;

    private final ModernTabButton testCasesBtn;
    private final ModernTabButton testRunsBtn;

    public Tabs(ProjectPanel projectPanel) {
        PropertiesComponent preference = PropertiesComponent.getInstance();
        String lastTab = preference.getValue("testGit.activeTab", "Test Cases");

        // 1. Setup Card Layout
        cardLayout = new CardLayout();
        contentPanel = new JBPanel<>(cardLayout);

        // ENHANCEMENT: Remove the ugly default inset border from the scroll panes so they sit flush
        contentPanel.add(createBorderlessScrollPane(projectPanel.getTestCaseTabController().getTree()), "Test Cases");
        contentPanel.add(createBorderlessScrollPane(projectPanel.getTestRunTabController().getTree()), "Test Runs");

        // 2. Setup Modern 50/50 Tab Bar
        JBPanel<?> tabBar = new JBPanel<>(new GridLayout(1, 2));
        tabBar.setBorder(JBUI.Borders.customLine(JBColor.border(), 0, 0, 1, 0));

        testCasesBtn = new ModernTabButton("Test Cases");
        testRunsBtn = new ModernTabButton("Test Runs");

        tabBar.add(testCasesBtn);
        tabBar.add(testRunsBtn);

        // 3. Click Listeners
        testCasesBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    selectTab("Test Cases", testCasesBtn, testRunsBtn, preference);
                }
            }
        });

        testRunsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    selectTab("Test Runs", testRunsBtn, testCasesBtn, preference);
                }
            }
        });

        // 4. Initial selection
        if ("Test Runs".equals(lastTab)) {
            selectTab("Test Runs", testRunsBtn, testCasesBtn, null);
        } else {
            selectTab("Test Cases", testCasesBtn, testRunsBtn, null);
        }

        // 5. Build Main Panel
        mainPanel = new JBPanel<>(new BorderLayout());
        mainPanel.add(tabBar, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void selectTab(String tabName, ModernTabButton activeBtn, ModernTabButton inactiveBtn, PropertiesComponent pref) {
        activeBtn.setActive(true);
        inactiveBtn.setActive(false);
        cardLayout.show(contentPanel, tabName);
        if (pref != null) pref.setValue("testGit.activeTab", tabName);
    }

    // Helper to create clean scroll panes
    private JBScrollPane createBorderlessScrollPane(JComponent content) {
        JBScrollPane scrollPane = new JBScrollPane(content);
        scrollPane.setBorder(JBUI.Borders.empty()); // Removes the native border
        return scrollPane;
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    // ==========================================
    // CUSTOM LIGHTWEIGHT MODERN TAB COMPONENT
    // ==========================================
    private static class ModernTabButton extends JLabel {
        private boolean active = false;
        private boolean hovered = false;

        public ModernTabButton(String text) {
            super(text, SwingConstants.CENTER);
            setFont(JBFont.label().deriveFont(Font.BOLD));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // ENHANCEMENT: Use JBUI.scale() for High-DPI / 4K monitor support
            setBorder(JBUI.Borders.empty(JBUI.scale(10), 0));
            setForeground(UIUtil.getContextHelpForeground());

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.active = active;
            this.setForeground(active ? UIUtil.getLabelForeground() : UIUtil.getContextHelpForeground());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            // ENHANCEMENT: Create a Graphics2D copy to prevent resource leaking & enable antialiasing
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (hovered && !active) {
                    g2.setColor(JBColor.namedColor("Button.hoverBackground", new JBColor(new Color(0, 0, 0, 15), new Color(255, 255, 255, 15))));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                super.paintComponent(g2); // Pass the upgraded g2 object

                if (active) {
                    g2.setColor(JBColor.namedColor("TabbedPane.underlineColor", JBColor.BLUE));
                    int lineThickness = JBUI.scale(3);
                    g2.fillRect(0, getHeight() - lineThickness, getWidth(), lineThickness);
                }
            } finally {
                // Always dispose of custom graphics copies
                g2.dispose();
            }
        }
    }
}