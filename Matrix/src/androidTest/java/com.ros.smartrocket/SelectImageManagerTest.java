package com.ros.smartrocket;

import android.app.Dialog;

import com.ros.smartrocket.activity.MainActivity;
import com.ros.smartrocket.utils.SelectImageManager;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SelectImageManagerTest {

    @Test
    public void testImageManager() {
        MainActivity activity = new MainActivity();

        SelectImageManager selectImageManager = SelectImageManager.getInstance();

        Dialog dialog = selectImageManager.showSelectImageDialog(activity, true);
        Assert.assertTrue(dialog.isShowing());
        dialog.dismiss();
        Assert.assertTrue(!dialog.isShowing());
    }

}
