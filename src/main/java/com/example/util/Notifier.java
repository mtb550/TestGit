package com.example.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notifier {
    public static void notify(Project project, String title, String content, NotificationType type) {
        Notification notification = NotificationGroupManager.getInstance()
                .getNotificationGroup("Test Case Notifications")
                .createNotification(title, content, type);
        notification.notify(project);
    }
}
