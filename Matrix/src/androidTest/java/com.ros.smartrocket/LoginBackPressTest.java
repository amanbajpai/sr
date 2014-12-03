package com.ros.smartrocket;

import com.ros.smartrocket.activity.LoginActivity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LoginBackPressTest {
    LoginActivity activity;

    @Before
    public void setUp() {
        activity = new LoginActivity();
        activity.onCreate(null);
    }

    @Test
    public void testBackButtonCloseActivityIfNoParent() throws Exception {
        activity.onBackPressed();

        Assert.assertTrue(activity.isFinishing());
    }
}
