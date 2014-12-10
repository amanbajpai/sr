package com.ros.smartrocket;

import android.test.AndroidTestCase;

import com.ros.smartrocket.utils.ChinaTransformLocation;

import junit.framework.Assert;

public class TransformChinaLocationTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testIsOutOfChina() {
        boolean isOutOfChina = ChinaTransformLocation.outOfChina(50.4263436, 30.5040258);
        Assert.assertTrue(isOutOfChina);
    }
}
