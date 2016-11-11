package com.ros.smartrocket;

import android.content.Intent;

import com.ros.smartrocket.activity.LaunchActivity;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.utils.NotificationUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GenerateNotificationTest {
    private LaunchActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(LaunchActivity.class).get();
    }

    @Test
    public void testGeneratePushNotification() {
        Intent intent = new Intent(activity, MainActivity.class);

        Boolean result = NotificationUtils.generateNotification(activity, "title", "message", intent);
        Assert.assertTrue(result);
    }
}
