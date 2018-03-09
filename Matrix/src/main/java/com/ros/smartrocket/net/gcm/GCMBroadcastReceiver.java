package com.ros.smartrocket.net.gcm;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class GCMBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
        intent.setComponent(comp);
        GCMIntentService.enqueueWork(context, intent);
    }
}