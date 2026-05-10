package org.testin.actions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;
import org.testin.pojo.dto.TestCaseDto;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

// todo, to be enhanced, make it dynamic for re-use, add horizontal scroll, add remain columns, make all cells editable.
public class ExcelPreviewDialog extends DialogWrapper {
    private final Map<String, List<TestCaseDto>> sheetsData;

    public ExcelPreviewDialog(@Nullable final Project project, final Map<String, List<TestCaseDto>> sheetsData) {
        super(project, true);
        this.sheetsData = sheetsData;

        setTitle("Preview & Edit Excel Import");
        setOKButtonText("Import All");
        setCancelButtonText("Cancel");

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(800, 400));

        JBTabbedPane tabbedPane = new JBTabbedPane();

        String[] columns = {"#", "Description", "Priority", "Expected Result", "Steps Count"};

        for (Map.Entry<String, List<TestCaseDto>> entry : sheetsData.entrySet()) {
            String sheetName = entry.getKey();
            List<TestCaseDto> testCases = entry.getValue();

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

            tabbedPane.addTab(sheetName, scrollPane);
        }

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }
}