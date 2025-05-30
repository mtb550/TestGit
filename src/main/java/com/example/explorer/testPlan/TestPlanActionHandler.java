package com.example.explorer.testPlan;

import com.example.pojo.TestCase;
import com.example.pojo.TestPlan;
import com.example.util.sql;
import com.google.gson.Gson;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;
import lombok.Setter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TestPlanActionHandler {
    private final TestPlan plan;
    private final ConfigPanel configPanel;
    private JTextField buildField;
    @Setter
    private CheckboxTree checkboxTree;
    @Setter
    private CheckedTreeNode rootNode;

    public TestPlanActionHandler(TestPlan plan, ConfigPanel configPanel) {
        this.plan = plan;
        this.configPanel = configPanel;
    }

    public JComponent createBuildNumberPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel("🔢 Build Number:"), BorderLayout.NORTH);
        buildField = new JTextField();
        panel.add(buildField, BorderLayout.CENTER);
        return panel;
    }

    public void handleOkAction(JComponent parent) {
        if (!validateInputs()) return;

        List<String> selectedCaseIds = collectSelectedTestCases();
        if (!validateTestCases(selectedCaseIds)) return;

        createTestRun(selectedCaseIds, parent);
    }

    private boolean validateInputs() {
        if (buildField.getText().trim().isEmpty()) {
            Messages.showWarningDialog("Please enter a build number.", "Validation");
            return false;
        }
        return true;
    }

    private boolean validateTestCases(List<String> selectedCaseIds) {
        if (selectedCaseIds.isEmpty()) {
            Messages.showWarningDialog("Please select at least one test case.", "Validation");
            return false;
        }
        return true;
    }

    private List<String> collectSelectedTestCases() {
        List<String> selectedCaseIds = new ArrayList<>();
        collectSelectedTestCases(rootNode, selectedCaseIds);
        return selectedCaseIds;
    }

    private void collectSelectedTestCases(CheckedTreeNode node, List<String> output) {
        Enumeration<?> enumeration = node.children();
        while (enumeration.hasMoreElements()) {
            Object child = enumeration.nextElement();
            if (child instanceof CheckedTreeNode ctNode) {
                Object userObject = ctNode.getUserObject();
                if (ctNode.isChecked() && userObject instanceof TestCase testCase) {
                    output.add(testCase.getId());
                }
                collectSelectedTestCases(ctNode, output);
            }
        }
    }

    private void createTestRun(List<String> selectedCaseIds, JComponent parent) {
        sql db = new sql();
        try {
            String configJson = createConfigJson();
            int testRunId = createTestRunRecord(db);
            addTestCasesToRun(db, testRunId, selectedCaseIds, configJson);

            Messages.showInfoMessage("Test Run created and test cases added.", "Success");
            updateParentTree(parent);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            Messages.showErrorDialog("Failed to create test run.", "Error");
        }
    }

    private String createConfigJson() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("platform", configPanel.getPlatform());
        configMap.put("language", configPanel.getLanguage());
        configMap.put("browser", configPanel.getBrowser());
        configMap.put("deviceType", configPanel.getDeviceType());
        return new Gson().toJson(configMap);
    }

    private int createTestRunRecord(sql db) throws Exception {
        db.execute("INSERT INTO nafath_tp_tree (name, type, link, project_id, created_by, created_at) VALUES (?, 1, ?, ?, ?, datetime('now'))",
                buildField.getText().trim(), plan.getId(), plan.getProject_id(), System.getProperty("user.name"));
        return db.get("SELECT last_insert_rowid()").asType(Integer.class);
    }

    private void addTestCasesToRun(sql db, int testRunId, List<String> selectedCaseIds, String configJson) throws Exception {
        for (int i = 0; i < selectedCaseIds.size(); i++) {
            db.execute("INSERT INTO nafath_tp (plan_id, test_case_id, config, run_order) VALUES (?, ?, ?, ?)",
                    testRunId, selectedCaseIds.get(i), configJson, i + 1);
        }
    }

    private void updateParentTree(JComponent parent) {
        if (parent instanceof JTree tree) {
            DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("Test Plans");
            TestPlan[] updatedPlans = new sql().get("SELECT * FROM nafath_tp_tree").as(TestPlan[].class);
            for (TestPlan updatedPlan : updatedPlans) {
                newRoot.add(new DefaultMutableTreeNode(updatedPlan));
            }
            DefaultTreeModel updatedModel = new DefaultTreeModel(newRoot);
            tree.setModel(updatedModel);
            tree.setRootVisible(true);
        }
    }
}