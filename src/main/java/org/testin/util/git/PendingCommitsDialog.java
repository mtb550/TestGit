package org.testin.util.git;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PendingCommitsDialog extends DialogWrapper {

    private final List<TestCaseDiff> differences;

    public PendingCommitsDialog(@Nullable Project project, List<TestCaseDiff> differences) {
        super(project, true);
        this.differences = differences;
        setTitle("Pending Test Case Changes");
        setOKButtonText("Push Changes");
        setCancelButtonText("Cancel");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Test Case ID", "Change Type", "Field", "Old Value", "New Value"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (TestCaseDiff diff : differences) {
            for (TestCaseDiff.FieldChange fc : diff.fieldChanges()) {
                model.addRow(new Object[]{
                        diff.testCaseId(),
                        diff.type().name(),
                        fc.fieldName(),
                        fc.oldValue(),
                        fc.newValue()
                });
            }
        }

        JBTable table = new JBTable(model);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // Type
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Field
        table.getColumnModel().getColumn(3).setPreferredWidth(250); // Old
        table.getColumnModel().getColumn(4).setPreferredWidth(250); // New

        panel.add(new JBScrollPane(table), BorderLayout.CENTER);
        panel.setPreferredSize(new Dimension(800, 400));

        return panel;
    }
}