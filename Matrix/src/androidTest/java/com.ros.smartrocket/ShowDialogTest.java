package com.ros.smartrocket;

import android.app.Dialog;
import android.content.Context;
import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.utils.DialogUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.junit.*;

import com.ros.smartrocket.bl.LoginBL;

import java.lang.NullPointerException;
import java.lang.System;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class ShowDialogTest {

    @Test
    public void testLocationDialog() {
        MainActivity activity = new MainActivity();

        Dialog dialog = DialogUtils.showLocationDialog(activity, true);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testNetworkDialog() {
        MainActivity activity = new MainActivity();

        Dialog dialog = DialogUtils.showNetworkDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testGoogleSdkDialog() {
        MainActivity activity = new MainActivity();

        Dialog dialog = DialogUtils.showGoogleSdkDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testMockLocationDialog() {
        MainActivity activity = new MainActivity();

        Dialog dialog = DialogUtils.showMockLocationDialog(activity, true);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testLoginFailedDialog() {
        Context context = Robolectric.application;

        Dialog dialog = DialogUtils.showLoginFailedDialog(context);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testAccountNotActivatedDialog() {
        MainActivity activity = new MainActivity();

        Dialog dialog = DialogUtils.showAccountNotActivatedDialog(activity);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testPhotoCanNotBeAddDialog() {
        Context context = Robolectric.application;

        Dialog dialog = DialogUtils.showPhotoCanNotBeAddDialog(context);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void test3GLimitExceededDialog() {
        Context context = Robolectric.application;

        Dialog dialog = DialogUtils.show3GLimitExceededDialog(context, null);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testQuiteTaskDialog() {
        MainActivity activity = new MainActivity();

        Dialog dialog = DialogUtils.showQuiteTaskDialog(activity, 0, 0);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

    @Test
    public void testReCheckAnswerTaskDialog() {
        MainActivity activity = new MainActivity();

        Dialog dialog = DialogUtils.showReCheckAnswerTaskDialog(activity, 0, 0);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }
}
