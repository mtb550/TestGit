package testGit.projectPanel;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.TabsListener;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.intellij.ui.treeStructure.SimpleTree;
import lombok.Getter;
import testGit.pojo.Directory;
import testGit.projectPanel.projectSelector.ProjectSelector;
import testGit.projectPanel.testCaseTab.ShortcutHandler;
import testGit.projectPanel.testCaseTab.TestCaseRenderer;
import testGit.projectPanel.testRunTab.MouseAdapterImpl;
import testGit.projectPanel.testRunTab.TestRunRenderer;
import testGit.projectPanel.versionSelector.VersionSelector;
import testGit.util.TestCasesDirectoryMapper;
import testGit.util.TestRunsDirectoryMapper;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


@Getter
public class ProjectPanel {
    public final static SimpleTree testCaseTree = new SimpleTree();
    public final static SimpleTree testRunTree = new SimpleTree();
    private final JBPanel<?> panel;
    private final ProjectSelector projectSelector;
    private final VersionSelector versionSelector;
    private final JBTabsImpl tabs;

    public ProjectPanel(final Project project) {
        System.out.println("Panel.Panel()");
        panel = new JBPanel<>(new BorderLayout());

        projectSelector = new ProjectSelector(this);

        setupTestCaseTree();
        setupTestRunTree();

        JBScrollPane testCaseScrollPane = new JBScrollPane(testCaseTree);
        JBScrollPane testRunScrollPane = new JBScrollPane(testRunTree);

        JBPanel<?> topBar = new JBPanel<>(new BorderLayout());

        topBar.add(projectSelector.selected(), BorderLayout.NORTH);

        Directory selectedProject = ProjectSelector.getSelectedProject();

        versionSelector = new VersionSelector(selectedProject);
        topBar.add(versionSelector.getComponent(), BorderLayout.SOUTH);

        panel.add(topBar, BorderLayout.NORTH);

        tabs = new JBTabsImpl(project);

        TabInfo testCasesTab = new TabInfo(testCaseScrollPane)
                .setText("Test Cases")
                .setIcon(AllIcons.Nodes.Folder);

        TabInfo testRunsTab = new TabInfo(testRunScrollPane)
                .setText("Test Runs")
                .setIcon(AllIcons.Nodes.Artifact);

        tabs.addTab(testCasesTab);
        tabs.addTab(testRunsTab);

        // 1. Get the instance of PropertiesComponent
        PropertiesComponent preference = PropertiesComponent.getInstance();

        // 2. Retrieve the value using a unique key (it's good practice to prefix with your plugin name)
        String lastTab = preference.getValue("testGit.activeTab", "Test Cases");

        // 3. Apply the selection to your tabs
        if ("Test Runs".equals(lastTab)) {
            tabs.select(testRunsTab, true);
        } else {
            tabs.select(testCasesTab, true);
        }

// 4. Update the listener to save changes using PropertiesComponent
        tabs.addListener(new TabsListener() {
            @Override
            public void selectionChanged(TabInfo oldSelection, TabInfo newSelection) {
                System.out.println("tabs.addListener.selectionChanged(): " + newSelection.getText());

                // Use setValue to persist the selection
                preference.setValue("testGit.activeTab", newSelection.getText());
            }
        });

        panel.add(tabs.getComponent(), BorderLayout.CENTER);
    }

    public void setupTestCaseTree() {
        System.out.println("Panel.setupTestCaseTree()");

        TestCasesDirectoryMapper.buildTreeAsync(testCaseTree);
        testCaseTree.setModel(TestCasesDirectoryMapper.getTreeModel());
        testCaseTree.setRootVisible(true);
        testCaseTree.setShowsRootHandles(true);
        testCaseTree.setDragEnabled(true);
        testCaseTree.setDropMode(DropMode.ON_OR_INSERT);

        Set<DefaultMutableTreeNode> sharedCutNodes = new HashSet<>();
        TransferHandlerImpl transferHandler = new TransferHandlerImpl(testCaseTree, sharedCutNodes);
        testCaseTree.setTransferHandler(transferHandler);
        ShortcutHandler.register(this, testCaseTree, transferHandler);
        testCaseTree.setCellRenderer(new TestCaseRenderer(sharedCutNodes));

        testCaseTree.addMouseListener(new testGit.projectPanel.testCaseTab.MouseAdapterImpl(this));
    }

    private void setupTestRunTree() {
        System.out.println("Panel.setupTestRunTree()");

        TestRunsDirectoryMapper.buildTreeAsync(testRunTree);
        testRunTree.setModel(TestRunsDirectoryMapper.getTreeModel());
        testRunTree.setRootVisible(true);
        testGit.projectPanel.testRunTab.ShortcutHandler.register(testRunTree);
        testRunTree.addMouseListener(new MouseAdapterImpl(this));
        testRunTree.setShowsRootHandles(true);
        testRunTree.setCellRenderer(new TestRunRenderer());
        testRunTree.addTreeSelectionListener(e -> {
        });
    }

}