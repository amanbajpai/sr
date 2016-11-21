package com.ros.smartrocket.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;

import com.google.analytics.tracking.android.EasyTracker;
import com.ros.smartrocket.R;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.net.NetworkService;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends ActionBarActivity {

    public static final String KEY_SAVED_ACTIVITY_INTENT = "KEY_SAVED_ACTIVITY_INTENT";
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private List<NetworkOperationListenerInterface> networkOperationListeners =
            new ArrayList<>();
    private boolean checkDeviceSettingsByOnResume = true;

    public BaseActivity() {
    }

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
        if (checkDeviceSettingsByOnResume) {
            if (UIUtils.isMockLocationEnabled(this)) {
                DialogUtils.showMockLocationDialog(this, false);
            } else if (!UIUtils.isAllLocationSourceEnabled(this) && preferencesManager.getUseLocationServices()) {
                DialogUtils.showLocationDialog(this, false);
            }
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, filter);
//        TCAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);
        super.onPause();
//        TCAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        EasyTracker.getInstance(this).activityStop(this);
        super.onStop();
    }

    public void checkDeviceSettingsByOnResume(boolean check) {
        this.checkDeviceSettingsByOnResume = check;
    }

    public void sendNetworkOperation(BaseOperation operation) {
        if (operation != null) {
            Intent intent = new Intent(this, NetworkService.class);
            intent.putExtra(NetworkService.KEY_OPERATION, operation);
            startService(intent);
        }
    }

    class NetworkBroadcastReceiver extends BroadcastReceiver {
        public NetworkBroadcastReceiver() {
        }

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
        if (!networkOperationListeners.contains(listener)) {
            networkOperationListeners.add(listener);
        }
    }

    public void removeNetworkOperationListener(NetworkOperationListenerInterface listener) {
        networkOperationListeners.remove(listener);
    }
}
