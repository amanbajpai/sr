package com.ros.smartrocket;

import com.ros.smartrocket.activity.LaunchActivity;
import org.junit.Before;
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

//    @Test
//    public void testImageManager() {
//        SelectImageManager selectImageManager = SelectImageManager.getInstance();
//
//        Dialog dialog = selectImageManager.showSelectImageDialog(activity, true, SelectImageManager.PREFIX_PROFILE,
//                imageCompleteListener);
//        Assert.assertTrue(dialog.isShowing());
//        dialog.dismiss();
//        Assert.assertTrue(!dialog.isShowing());
//    }
}