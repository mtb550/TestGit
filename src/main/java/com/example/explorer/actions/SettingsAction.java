package com.example.explorer.actions;

import com.example.util.sql;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;

public class SettingsAction extends AnAction {
    public SettingsAction() {
        super("Settings", "Configure tree", AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        JFrame frame = new JFrame("Settings");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        JBTabbedPane tabbedPane = new JBTabbedPane();

        // --- Projects Tab ---
        JBPanel<?> projectPanel = new JBPanel<>(new BorderLayout());
        JBTable projectTable = new JBTable();
        JBScrollPane projectScroll = new JBScrollPane(projectTable);
        projectPanel.add(projectScroll, BorderLayout.CENTER);

        JPanel projectButtons = new JPanel();
        JButton addProject = new JButton("Add");
        JButton editProject = new JButton("Edit");
        JButton deleteProject = new JButton("Delete");
        projectButtons.add(addProject);
        projectButtons.add(editProject);
        projectButtons.add(deleteProject);
        projectPanel.add(projectButtons, BorderLayout.SOUTH);

        tabbedPane.add("Projects", projectPanel);

        // --- Users Tab ---
        JPanel userPanel = new JPanel(new BorderLayout());
        JBTable userTable = new JBTable();
        JBScrollPane userScroll = new JBScrollPane(userTable);
        userPanel.add(userScroll, BorderLayout.CENTER);

        JPanel userButtons = new JPanel();
        JButton addUser = new JButton("Add");
        JButton editUser = new JButton("Edit");
        JButton deleteUser = new JButton("Delete");
        userButtons.add(addUser);
        userButtons.add(editUser);
        userButtons.add(deleteUser);
        userPanel.add(userButtons, BorderLayout.SOUTH);

        tabbedPane.add("Users", userPanel);

        frame.add(tabbedPane);
        frame.setVisible(true);

        // Load data
        loadProjects(projectTable);
        loadUsers(userTable);

        // Button actions
        addProject.addActionListener(evt -> showProjectDialog(null, projectTable));
        editProject.addActionListener(evt -> {
            int row = projectTable.getSelectedRow();
            if (row != -1) showProjectDialog(getRowId(projectTable, row), projectTable);
        });
        deleteProject.addActionListener(evt -> softDeleteProject(projectTable));

        addUser.addActionListener(evt -> showUserDialog(null, userTable));
        editUser.addActionListener(evt -> {
            int row = userTable.getSelectedRow();
            if (row != -1) showUserDialog(getRowId(userTable, row), userTable);
        });
        deleteUser.addActionListener(evt -> {
            int row = userTable.getSelectedRow();
            if (row != -1) {
                int id = getRowId(userTable, row);
                new sql().execute("DELETE FROM users WHERE id = ?", id);
                loadUsers(userTable);
            }
        });
    }

    private int getRowId(JTable table, int row) {
        return Integer.parseInt(table.getValueAt(row, 0).toString());
    }

    private void loadProjects(JTable table) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Active"}, 0);
        sql db = new sql().get("SELECT project_id, name, active FROM projects");

        for (HashMap<String, Object> row : db.dbResult) {
            model.addRow(new Object[]{
                    row.get("project_id"),
                    row.get("name"),
                    row.get("active")
            });
        }
        table.setModel(model);
    }

    private void loadUsers(JTable table) {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Role", "Email", "Enabled"}, 0);
        sql db = new sql().get("SELECT id, name, role, email, enabled FROM users");

        for (HashMap<String, Object> row : db.dbResult) {
            model.addRow(new Object[]{
                    row.get("id"),
                    row.get("name"),
                    row.get("role"),
                    row.get("email"),
                    row.get("enabled")
            });
        }
        table.setModel(model);
    }

    private void softDeleteProject(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = getRowId(table, row);
        new sql().execute("UPDATE projects SET active = 0 WHERE project_id = ?", id);
        loadProjects(table);
    }

    private void showProjectDialog(Integer id, JTable table) {
        String name = "";
        if (id != null) {
            sql db = new sql().get("SELECT name FROM projects WHERE project_id = ?", id);
            if (!db.dbResult.isEmpty()) {
                name = String.valueOf(db.dbResult.get(0).get("name"));
            }
        }

        String input = JOptionPane.showInputDialog(null, "Project Name:", name);
        if (input != null && !input.isBlank()) {
            if (id == null)
                new sql().execute("INSERT INTO projects (name, active) VALUES (?, 1)", input);
            else
                new sql().execute("UPDATE projects SET name = ? WHERE project_id = ?", input, id);
            loadProjects(table);
        }
    }

    private void showUserDialog(Integer id, JTable table) {
        JTextField nameField = new JTextField();
        JTextField roleField = new JTextField();
        JTextField emailField = new JTextField();
        JCheckBox enabledBox = new JCheckBox("Enabled");

        if (id != null) {
            sql db = new sql().get("SELECT name, role, email, enabled FROM users WHERE id = ?", id);
            if (!db.dbResult.isEmpty()) {
                HashMap<String, Object> row = db.dbResult.get(0);
                nameField.setText(String.valueOf(row.get("name")));
                roleField.setText(String.valueOf(row.get("role")));
                emailField.setText(String.valueOf(row.get("email")));
                enabledBox.setSelected(Integer.parseInt(row.get("enabled").toString()) == 1);
            }
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Role (number):"));
        panel.add(roleField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(enabledBox);

        int result = JOptionPane.showConfirmDialog(null, panel,
                id == null ? "Add User" : "Edit User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                if (id == null)
                    new sql().execute("INSERT INTO users (name, role, email, enabled) VALUES (?, ?, ?, ?)",
                            nameField.getText(), Integer.parseInt(roleField.getText()), emailField.getText(), enabledBox.isSelected() ? 1 : 0);
                else
                    new sql().execute("UPDATE users SET name = ?, role = ?, email = ?, enabled = ? WHERE id = ?",
                            nameField.getText(), Integer.parseInt(roleField.getText()), emailField.getText(), enabledBox.isSelected() ? 1 : 0, id);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
            loadUsers(table);
        }
    }
}
