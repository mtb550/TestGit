package testGit.actions.editorPanel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Config;
import testGit.pojo.TestCase;
import testGit.util.Notifier;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AddTestCaseAction extends AnAction {
    TestCase tc;
    JList<TestCase> list;
    String featurePath;
    VirtualFile file;
    DefaultListModel<TestCase> model;

    public AddTestCaseAction(String featurePath, @NotNull VirtualFile file, JList<TestCase> list, DefaultListModel<TestCase> model, TestCase tc) {
        super("➕ Add Test Case");
        this.tc = tc;
        this.list = list;
        this.file = file;
        this.featurePath = featurePath;
        this.model = model;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String title = JOptionPane.showInputDialog(list,
                "Enter title for new test case:",
                "New Test Case",
                JOptionPane.PLAIN_MESSAGE);

        if (title != null && !title.trim().isEmpty()) {

            // 1. Setup the new TestCase
            TestCase newCase = new TestCase();
            newCase.setTitle(title.trim());
            //newCase.setSteps("Step 1: ...");
            //newCase.setExpectedResult("Expected result...");
            newCase.setPriority("LOW");
            //newCase.setAutomationRef("");
            //newCase.setSort(model.getSize() + 1);
            newCase.setNext(null);
            newCase.setId(UUID.randomUUID().toString());

            /// here create json file
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .enable(SerializationFeature.INDENT_OUTPUT);
            try {
                System.out.println(featurePath);

                // 2. Handle the linking logic
                if (model.isEmpty()) {
                    // First item in the folder
                    newCase.setIsHead(true);
                } else {
                    // Chain the previous last item to this new item
                    newCase.setIsHead(false);

                    TestCase lastItem = model.getElementAt(model.getSize() - 1);
                    lastItem.setNext(UUID.fromString(newCase.getId()));

                    // We MUST update the previous last item's JSON file on disk
                    File lastItemFile = new File(featurePath, lastItem.getId() + ".json");
                    mapper.writeValue(lastItemFile, lastItem);
                }

                // 3. Save the new TestCase JSON
                File targetFile = new File(featurePath, newCase.getId() + ".json");
                mapper.writeValue(targetFile, newCase);

                // 4. Update UI and VFS
                LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);

                model.addElement(newCase);
                list.ensureIndexIsVisible(model.getSize() - 1);
                list.setSelectedIndex(model.getSize() - 1);

                System.out.println("JSON file created successfully!");

            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                Notifier.notify(Config.getProject(), "Test Case Notifications",
                        "Error", "Failed to save test case: " + ex.getMessage(),
                        NotificationType.ERROR);
            }


        }
    }
}
