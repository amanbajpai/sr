package com.matrix;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.junit.*;

@RunWith(RobolectricTestRunner.class)
@Config (manifest=Config.NONE)
public class BaseTest {

    @Test
    public void shouldComplete() {
        Assert.assertTrue(1 == 1);
    }
}
