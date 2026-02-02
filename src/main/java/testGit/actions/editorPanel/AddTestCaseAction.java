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
import testGit.ui.AddNewTestCaseDialog;
import testGit.util.Notifier;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AddTestCaseAction extends AnAction {
    private final JList<TestCase> list;
    private final String featurePath;
    private final DefaultListModel<TestCase> model;

    public AddTestCaseAction(String featurePath, @NotNull VirtualFile file, JList<TestCase> list, DefaultListModel<TestCase> model) {
        super("➕ Add Test Case");
        this.list = list;
        this.featurePath = featurePath;
        this.model = model;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AddNewTestCaseDialog dialog = new AddNewTestCaseDialog();

        if (dialog.showAndGet()) {
            saveTestCase(dialog.getInput());
        }
    }

    private void saveTestCase(String title) {
        TestCase newCase = new TestCase();
        newCase.setTitle(title);
        newCase.setPriority("LOW");
        newCase.setNext(null);
        newCase.setId(UUID.randomUUID().toString());

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT);
        try {
            if (model.isEmpty()) {
                newCase.setIsHead(true);
            } else {
                newCase.setIsHead(false);
                TestCase lastItem = model.getElementAt(model.getSize() - 1);
                lastItem.setNext(UUID.fromString(newCase.getId()));

                File lastItemFile = new File(featurePath, lastItem.getId() + ".json");
                mapper.writeValue(lastItemFile, lastItem);
            }

            File targetFile = new File(featurePath, newCase.getId() + ".json");
            mapper.writeValue(targetFile, newCase);

            LocalFileSystem.getInstance().refreshAndFindFileByIoFile(targetFile);
            model.addElement(newCase);
            list.ensureIndexIsVisible(model.getSize() - 1);

        } catch (IOException ex) {
            Notifier.notify(Config.getProject(), "Test Case Notifications", "Error", ex.getMessage(), NotificationType.ERROR);
        }
    }
}