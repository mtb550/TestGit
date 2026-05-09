package org.testin.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;
import org.testin.pojo.dto.TestCaseDto;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// todo, to be enhanced, make it dynamic for re-use, add horizontal scroll, add remain columns, make all cells editable.
public class ExcelPreviewDialog extends DialogWrapper {
    private final List<TestCaseDto> testCases;

    public ExcelPreviewDialog(@Nullable final Project project, final List<TestCaseDto> testCases) {
        super(project, true);
        this.testCases = testCases;

        setTitle("Preview & Edit Excel Import");
        setOKButtonText("Import");
        setCancelButtonText("Cancel");

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(800, 400));

        String[] columns = {"#", "Description", "Priority", "Expected Result", "Steps Count"};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        int index = 1;
        for (TestCaseDto tc : testCases) {
            String priority = tc.getPriority().name();
            String stepsCount = tc.getSteps().size() + " Steps";

            model.addRow(new Object[]{
                    index++,
                    tc.getDescription(),
                    priority,
                    tc.getExpectedResult(),
                    stepsCount
            });
        }

        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();

                if (row >= 0 && col == 1) {
                    String updatedDescription = (String) model.getValueAt(row, col);
                    testCases.get(row).setDescription(updatedDescription);
                }
            }
        });

        JBTable table = new JBTable(model);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        JBScrollPane scrollPane = new JBScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}