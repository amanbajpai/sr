package com.ros.smartrocket;

import android.content.Context;
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
public class PreLoginTest {

    @Test
    public void testLoginWithoutNetwork() {

        Context context = Robolectric.application;

        //TODO: Mock Network + GPS functionality to make it work
        LoginBL lBL = new LoginBL();
        LoginBL.PreLoginErrors error = lBL.login(context, "Agent1", "123456");
        System.out.println("[error=" + error + "]");
        Assert.assertTrue(error == LoginBL.PreLoginErrors.SUCCESS);
    }
}
