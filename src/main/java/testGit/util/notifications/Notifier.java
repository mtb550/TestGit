package testGit.util.notifications;

import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationAction;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import testGit.pojo.Config;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;

public class Notifier {

    public static void info(String title, String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("TestGit Notifications")
                .createNotification(title, message, NotificationType.INFORMATION)
                .notify(Config.getProject());
    }

    public static void warn(String title, String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("TestGit Notifications")
                .createNotification(title, message, NotificationType.WARNING)
                .notify(Config.getProject());
    }

    public static void error(String title, String message) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("TestGit Notifications")
                .createNotification(title, message, NotificationType.ERROR)
                .notify(Config.getProject());
    }

    public static void infoWithAction(String title, String message, String actionName, Runnable action) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("TestGit Notifications")
                .createNotification(title, message, NotificationType.INFORMATION);

        notification.addAction(NotificationAction.createSimple(actionName, action));
        notification.notify(Config.getProject());
    }

    public static void infoWithOpenAndCopy(String title, String message, File file) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("TestGit Notifications")
                .createNotification(title, message, NotificationType.INFORMATION);

        notification.addAction(NotificationAction.createSimple("Open report", () -> BrowserUtil.browse(file)));
        NotificationAction copyAction = new NotificationAction("Copy path") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e, @NotNull Notification notification) {
                CopyPasteManager.getInstance().setContents(new StringSelection(file.getAbsolutePath()));
            }
        };
        copyAction.getTemplatePresentation().setIcon(AllIcons.Actions.Copy);
        notification.addAction(copyAction);

        notification.notify(Config.getProject());
    }

    // TODO: try it
    public static void showFloatingHint(JComponent targetComponent, String message, MessageType type) {
        JBPopupFactory.getInstance()
                //.createBalloonBuilder()
                //.createDialogBalloonBuilder()
                .createHtmlTextBalloonBuilder(message, type, null)
                .setFadeoutTime(3000) // Disappears after 3 seconds
                .createBalloon()
                .show(RelativePoint.getCenterOf(targetComponent), Balloon.Position.above);
    }
}