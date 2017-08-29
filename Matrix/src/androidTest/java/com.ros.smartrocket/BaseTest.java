package com.ros.smartrocket;

import junit.framework.Assert;

import org.junit.Test;

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
