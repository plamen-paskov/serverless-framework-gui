package io.solidloop.jetbrains.ide.serverlessframeworkgui;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

public class NotificationUtil {
    public static void displayError(String title, String content) {
        Notifications.Bus.notify(new Notification("Serverless Framework GUI", title, content, NotificationType.ERROR));
    }
}
