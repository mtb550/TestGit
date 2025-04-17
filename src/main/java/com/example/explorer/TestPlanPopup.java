package com.example.explorer;

import com.example.pojo.TestPlan;
import com.example.pojo.Tree;
import com.example.util.NodeType;
import com.example.util.sql;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.*;

import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TestPlanPopup {

    public static void showFolderInfo(TestPlan plan, JComponent parent) {
        DialogWrapper dialog = new DialogWrapper(true) {
            {
                init();
                setTitle("Add Test Cases to Plan");
            }

            @Override
            protected JComponent createCenterPanel() {
                JBPanel<?> panel = new JBPanel<>(new BorderLayout(10, 10));
                panel.setPreferredSize(new Dimension(450, 500));
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));

                // === Build Number
                JBPanel<?> buildPanel = new JBPanel<>(new BorderLayout(5, 5));
                JBLabel buildLabel = new JBLabel("🔢 Build Number:");
                JBTextField buildField = new JBTextField();
                buildPanel.add(buildLabel, BorderLayout.NORTH);
                buildPanel.add(buildField, BorderLayout.CENTER);
                panel.add(buildPanel, BorderLayout.NORTH);

                // === Test Case List
                JBPanel<?> casePanel = new JBPanel<>();
                casePanel.setLayout(new BoxLayout(casePanel, BoxLayout.Y_AXIS));
                JBLabel caseLabel = new JBLabel("🧪 Select Test Cases:");
                caseLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
                casePanel.add(caseLabel);

                List<JBCheckBox> checkBoxes = new ArrayList<>();
                Tree[] testCases = new sql().get("SELECT * FROM tree WHERE type = ?", NodeType.FEATURE.getCode()).as(Tree[].class);
                for (Tree tc : testCases) {
                    if (tc.getLink() == plan.getProject_id()) {
                        JBCheckBox cb = new JBCheckBox(tc.getName());
                        cb.putClientProperty("caseId", tc.getId());
                        checkBoxes.add(cb);
                        casePanel.add(cb);
                    }
                }

                JBScrollPane scrollPane = new JBScrollPane(casePanel);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                panel.add(scrollPane, BorderLayout.CENTER);

                // === Add Button
                JBPanel<?> buttonPanel = new JBPanel<>(new FlowLayout(FlowLayout.RIGHT));
                JButton addButton = new JButton("➕ Add");
                buttonPanel.add(addButton);
                panel.add(buttonPanel, BorderLayout.SOUTH);

                // === Add logic
                addButton.addActionListener(e -> {
                    String buildNumber = buildField.getText().trim();
                    if (buildNumber.isBlank()) {
                        Messages.showWarningDialog("Please enter a build number.", "Validation");
                        return;
                    }

                    List<Integer> selectedIds = new ArrayList<>();
                    for (JBCheckBox cb : checkBoxes) {
                        if (cb.isSelected()) {
                            selectedIds.add((Integer) cb.getClientProperty("caseId"));
                        }
                    }

                    if (selectedIds.isEmpty()) {
                        Messages.showWarningDialog("Please select at least one test case.", "Validation");
                        return;
                    }

                    sql db = new sql();
                    db.execute("UPDATE nafath_tp_tree SET build_number = ? WHERE id = ?", buildNumber, plan.getId());

                    for (int i = 0; i < selectedIds.size(); i++) {
                        db.execute("INSERT INTO nafath_tp_testcases (plan_id, test_case_id, run_order) VALUES (?, ?, ?)",
                                plan.getId(), selectedIds.get(i), i + 1);
                    }

                    Messages.showInfoMessage("Test cases added to plan successfully.", "Success");
                    close(OK_EXIT_CODE);
                });

                return panel;
            }
        };

        dialog.show();
    }
}
