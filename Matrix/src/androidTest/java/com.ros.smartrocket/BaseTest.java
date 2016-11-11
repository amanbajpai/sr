package com.ros.smartrocket;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BaseTest {

    @Test
    public void shouldComplete() {
        Assert.assertTrue(1 == 1);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowException() {
        Object o = null;
        o.toString();
    }
}
