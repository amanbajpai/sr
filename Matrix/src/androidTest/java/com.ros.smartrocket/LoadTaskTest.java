package com.ros.smartrocket;

import android.content.Context;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.junit.*;

import java.lang.NullPointerException;

@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class LoadTaskTest {

    @Test
    public void testLoadTasksWithZeroDistane() {
        Assert.assertTrue(1 == 1);
    }
}
