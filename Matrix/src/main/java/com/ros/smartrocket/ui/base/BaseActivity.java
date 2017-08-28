package com.ros.smartrocket.ui.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.ros.smartrocket.App;
import com.ros.smartrocket.ui.dialog.CustomProgressDialog;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.net.NetworkService;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.MatrixContextWrapper;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity implements MvpView {

    public static final String KEY_SAVED_ACTIVITY_INTENT = "KEY_SAVED_ACTIVITY_INTENT";
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private CustomProgressDialog progressDialog;
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkDeviceSettingsByOnResume) {
            if (UIUtils.isMockLocationEnabled(this, App.getInstance().getLocationManager().getLocation())) {
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
                Stream.of(networkOperationListeners)
                        .filter(netListener -> netListener != null)
                        .forEach(netListener -> {
                            if (operation.isSuccess()) {
                                netListener.onNetworkOperationSuccess(operation);
                            } else {
                                netListener.onNetworkOperationFailed(operation);
                            }
                        });
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

    protected void showNetworkError(BaseOperation operation) {
        UIUtils.showSimpleToast(this, operation.getResponseError(), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LocaleUtils.setCurrentLanguage();
        Locale newLocale = LocaleUtils.getCurrentLocale();
        Context context = MatrixContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }

    @Override
    public void showLoading(boolean isCancelable) {
        progressDialog = CustomProgressDialog.show(this);
        progressDialog.setCancelable(isCancelable);
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
