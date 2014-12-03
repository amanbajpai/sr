package com.ros.smartrocket;

import com.ros.smartrocket.utils.UIUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@org.robolectric.annotation.Config(emulateSdk = 18, reportSdk = 18)
public class IsoTimeToLongTest extends TestCase {

    @Test
    public void testIsoTimeToLong() {
        String isoTime = "2014-03-03T09:48:51.7158000+02:00";
        Assert.assertTrue(UIUtils.isoTimeToLong(isoTime) != 0);
    }
}
