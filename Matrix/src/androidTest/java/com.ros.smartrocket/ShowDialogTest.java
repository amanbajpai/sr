package com.ros.smartrocket;

import android.app.Dialog;

import com.ros.smartrocket.activity.LaunchActivity;
import com.ros.smartrocket.utils.DialogUtils;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ShowDialogTest {
    private LaunchActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(LaunchActivity.class).get();
    }

    @Test
    public void testLocationDialog() {
        Dialog dialog = DialogUtils.showLocationDialog(activity, true);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testNetworkDialog() {
        Dialog dialog = DialogUtils.showNetworkDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testGoogleSdkDialog() {
        Dialog dialog = DialogUtils.showGoogleSdkDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testMockLocationDialog() {
        Dialog dialog = DialogUtils.showMockLocationDialog(activity, true);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testLoginFailedDialog() {
        Dialog dialog = DialogUtils.showLoginFailedDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testAccountNotActivatedDialog() {
        Dialog dialog = DialogUtils.showAccountNotActivatedDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testPhotoCanNotBeAddDialog() {
        Dialog dialog = DialogUtils.showPhotoCanNotBeAddDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void test3GLimitExceededDialog() {
        Dialog dialog = DialogUtils.show3GLimitExceededDialog(activity, null);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testQuiteTaskDialog() {
        Dialog dialog = DialogUtils.showQuiteTaskDialog(activity, 0, 0, 0);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }
}
