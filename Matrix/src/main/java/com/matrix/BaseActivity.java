package com.matrix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.net.NetworkService;
import com.matrix.utils.PreferencesManager;

import java.util.ArrayList;

public class BaseActivity extends ActionBarActivity {

    public static final String KEY_SAVED_ACTIVITY_INTENT = "KEY_SAVED_ACTIVITY_INTENT";

    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private ArrayList<NetworkOperationListenerInterface> networkOperationListeners = new ArrayList<NetworkOperationListenerInterface>();

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        saveActivityIntent();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        receiver = new NetworkBroadcastReceiver();
        filter = new IntentFilter(NetworkService.BROADCAST_ACTION);
    }

    protected void saveActivityIntent() {
        PreferencesManager preferences = PreferencesManager.getInstance();
        preferences.setString(KEY_SAVED_ACTIVITY_INTENT, getIntent().toUri(0));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }

    public void sendNetworkOperation(BaseOperation operation) {
        if (operation != null) {
            Intent intent = new Intent(this, NetworkService.class);
            intent.putExtra(NetworkService.KEY_OPERATION, operation);
            startService(intent);
        }
    }

    class NetworkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            BaseOperation operation = (BaseOperation) intent.getSerializableExtra(NetworkService.KEY_OPERATION);
            if (operation != null) {
                for (NetworkOperationListenerInterface netListener : networkOperationListeners) {
                    if (netListener != null) {
                        netListener.onNetworkOperation(operation);
                    }
                }
            }
        }
    }

    public void addNetworkOperationListener(NetworkOperationListenerInterface listener) {
        networkOperationListeners.add(listener);
    }

    public void removeNetworkOperationListener(NetworkOperationListenerInterface listener) {
        networkOperationListeners.remove(listener);
    }
}
