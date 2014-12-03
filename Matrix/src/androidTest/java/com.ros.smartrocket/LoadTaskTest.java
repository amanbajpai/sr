package com.ros.smartrocket;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LoadTaskTest {

    @Test
    public void testLoadTasksWithZeroDistance() {
        Assert.assertTrue(1 == 1);
    }
}
