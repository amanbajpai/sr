package com.ros.smartrocket.utils.eventbus;

import com.ros.smartrocket.presentation.notification.NotificationActivity;

public class CloseNotificationAction {
    private final NotificationActivity.NotificationType type;
    private final Integer taskId;

    public CloseNotificationAction(NotificationActivity.NotificationType type, Integer taskId) {
        this.type = type;
        this.taskId = taskId;
    }

    public NotificationActivity.NotificationType getType() {
        return type;
    }

    public Integer getTaskId() {
        return taskId;
    }
}
