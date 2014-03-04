package com.ros.smartrocket;

import com.ros.smartrocket.utils.UIUtils;
import junit.framework.Assert;
import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class IsoTimeToLongTest {

    @Test
    public void testIsoTimeToLong() {
        String isoTime = "2014-03-03T09:48:51.7158+02:00";
        Assert.assertTrue(UIUtils.isoTimeToLong(isoTime) != 0);
    }
}
