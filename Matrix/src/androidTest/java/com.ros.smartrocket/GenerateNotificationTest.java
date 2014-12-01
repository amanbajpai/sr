package com.ros.smartrocket;

import android.content.Context;
import android.content.Intent;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.utils.NotificationUtils;
import junit.framework.Assert;
import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.Boolean;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GenerateNotificationTest {

    @Test
    public void testGeneratePushNotification() {
        Context context = Robolectric.application;
        Intent intent = new Intent(context, MainActivity.class);

        Boolean result = NotificationUtils.generateNotification(context, "title", "message", intent);
        Assert.assertTrue(result);
    }
}
