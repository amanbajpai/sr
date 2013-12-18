package com.ros.smartrocket;

import android.content.Context;
import com.ros.smartrocket.Config;
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
public class BaseTest {

    @Test
    public void shouldComplete() {
        Assert.assertTrue(1 == 1);
    }

    @Test(expected= NullPointerException.class)
    public void shouldThrowException() {
        Object o = null;
        Context context = Robolectric.application;
        o.toString();
    }
}
