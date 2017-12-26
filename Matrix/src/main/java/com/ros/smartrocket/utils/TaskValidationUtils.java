package com.ros.smartrocket.utils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.task.Task;

public final class TaskValidationUtils {

    private void TaskValidationUtils() {
    }

    public static boolean isTaskReadyToSend() {
        return UIUtils.isOnline(App.getInstance())
                && UIUtils.isAllLocationSourceEnabled(App.getInstance())
                && PreferencesManager.getInstance().getUseLocationServices()
                && !UIUtils.isMockLocationEnabled(App.getInstance(), App.getInstance().getLocationManager().getLocation());
    }

    public static boolean isValidationLocationAdded(Task task) {
        return task.getLatitudeToValidation() != 0 && task.getLongitudeToValidation() != 0;
    }
}
