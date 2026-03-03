package testGit.projectPanel;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import lombok.Getter;

import javax.swing.*;

@Getter
public class Tabs {
    private final JBTabs tabs;
    private final TabInfo testCasesTab;
    private final TabInfo testRunsTab;

    public Tabs(Project project, ProjectPanel projectPanel) {
        this.tabs = JBTabsFactory.createTabs(project, projectPanel);
        PropertiesComponent preference = PropertiesComponent.getInstance();

        testCasesTab = new TabInfo(new JBScrollPane(projectPanel.getTestCaseTabController().getTree()))
                .setText("Test Cases")
                .setIcon(AllIcons.Nodes.Folder);

        testRunsTab = new TabInfo(new JBScrollPane(projectPanel.getTestRunTabController().getTree()))
                .setText("Test Runs")
                .setIcon(AllIcons.Nodes.Artifact);

        tabs.addTab(testCasesTab);
        tabs.addTab(testRunsTab);

        String lastTab = preference.getValue("testGit.activeTab", "Test Cases");
        tabs.select("Test Runs".equals(lastTab) ? testRunsTab : testCasesTab, true);

        tabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                preference.setValue("testGit.activeTab", newSelection.getText());
            }
        });
    }

    public JComponent getComponent() {
        return tabs.getComponent();
    }
}