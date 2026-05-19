package org.testin.projectPanel.versionSelector;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.NotNull;
import org.testin.pojo.Config;
import org.testin.pojo.dto.dirs.TestProjectDirectoryDto;
import org.testin.util.git.GitCommandRunner;
import org.testin.util.notifications.Notifier;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BranchSelector {
    private final boolean showRemote = false;

    private final ComboBox<String> comboBox;
    private final DefaultComboBoxModel<String> model;

    private Path projectPath;

    public BranchSelector(final TestProjectDirectoryDto testProjectDirectory) {
        this.model = new DefaultComboBoxModel<>();
        this.comboBox = new ComboBox<>(model);

        comboBox.setFocusable(false);
        comboBox.setEnabled(false);

        comboBox.addActionListener(this::onSelection);

        updateProject(testProjectDirectory);
    }

    public void updateProject(TestProjectDirectoryDto testProjectDirectory) {
        this.projectPath = testProjectDirectory != null ? testProjectDirectory.getPath() : null;

        model.removeAllElements();
        comboBox.setEnabled(false);

        if (projectPath != null) {
            File gitDir = new File(projectPath.toFile(), ".git");
            if (gitDir.exists() && gitDir.isDirectory()) {
                model.addElement("Loading branches...");
                loadGitBranches();
            } else {
                model.addElement("Not a Git repository");
            }
        } else {
            model.addElement("No project path found");
        }
    }

    private void onSelection(ActionEvent e) {
        String selectedBranch = getSelectedBranch();
        if (selectedBranch != null && !selectedBranch.equals("No branches found") && !selectedBranch.equals("Loading branches...")) {
            System.out.println("Selected Branch changed to: " + selectedBranch);
        }
    }

    private void loadGitBranches() {
        ProgressManager.getInstance().run(new Task.Backgroundable(Config.getProject(), "Fetching Git branches", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setIndeterminate(true);
                try {
                    String[] command = showRemote
                            ? new String[]{"git", "branch", "-a"}
                            : new String[]{"git", "branch"};

                    String output = GitCommandRunner.execute(projectPath, command);
                    List<String> branches = parseBranches(output);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        model.removeAllElements();

                        if (!branches.isEmpty()) {
                            for (String branch : branches) {
                                model.addElement(branch);
                            }
                            comboBox.setEnabled(true);
                            comboBox.setSelectedIndex(0);
                        } else {
                            model.addElement("No branches found");
                            comboBox.setEnabled(false);
                        }
                    });

                } catch (Exception ex) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        model.removeAllElements();
                        model.addElement("Failed to load branches");
                        comboBox.setEnabled(false);
                        Notifier.getInstance().error("Git Error", "Failed to load branches: " + ex.getMessage());
                    });
                }
            }
        });
    }

    private List<String> parseBranches(String commandOutput) {
        List<String> branchList = new ArrayList<>();
        if (commandOutput == null || commandOutput.trim().isEmpty()) {
            return branchList;
        }

        String[] lines = commandOutput.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            if (trimmed.startsWith("*")) {
                trimmed = trimmed.substring(1).trim();
            }

            if (showRemote && trimmed.contains("->")) {
                continue;
            }

            if (!branchList.contains(trimmed)) {
                branchList.add(trimmed);
            }
        }
        return branchList;
    }

    public JComponent getComponent() {
        return comboBox;
    }

    public String getSelectedBranch() {
        return (String) comboBox.getSelectedItem();
    }
}