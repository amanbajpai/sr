package com.ros.smartrocket;

import com.ros.smartrocket.utils.UIUtils;

import junit.framework.Assert;

import org.junit.Test;


public class LongToStringTimeTest {

    @Test
    public void testLongToStringTime() {
        String dateType0 = UIUtils.longToString(1393857783171L, 0);
        String dateType1 = UIUtils.longToString(1393857783171L, 1);
        String dateType2 = UIUtils.longToString(1393857783171L, 2);
        String dateType3 = UIUtils.longToString(1393857783171L, 3);

        Assert.assertTrue(dateType0.equals("16:43 PM"));
        /*Assert.assertTrue(dateType1.equals("03 Mar 14"));
        Assert.assertTrue(dateType2.equals("2014-03-03T16:43:03.171"));
        Assert.assertTrue(dateType3.equals("16:43 PM 03 Mar 14"));*/
    }
}
