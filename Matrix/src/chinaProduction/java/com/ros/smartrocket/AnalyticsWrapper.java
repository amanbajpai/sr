package com.ros.smartrocket;

import android.content.Context;

import com.tendcloud.tenddata.TCAgent;

public class AnalyticsWrapper {
    public static void initAnalytics(Context c){
        TCAgent.init(c, Keys.TALKING_DATA_CHINA, "");
    }
}
