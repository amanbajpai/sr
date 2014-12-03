package com.ros.smartrocket;

import android.app.Dialog;
import android.content.Context;

import com.ros.smartrocket.activity.LaunchActivity;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.utils.SelectImageManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SelectImageManagerTest {
    private LaunchActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(LaunchActivity.class).get();
    }

    @Test
    public void testImageManager() {
        SelectImageManager selectImageManager = SelectImageManager.getInstance();

        Dialog dialog = selectImageManager.showSelectImageDialog(activity, true);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

}
