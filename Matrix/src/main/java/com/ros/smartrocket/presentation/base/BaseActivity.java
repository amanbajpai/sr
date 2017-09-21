package com.ros.smartrocket.presentation.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ros.smartrocket.App;
import com.ros.smartrocket.ui.dialog.CustomProgressDialog;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.MatrixContextWrapper;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity implements MvpView {

    public static final String KEY_SAVED_ACTIVITY_INTENT = "KEY_SAVED_ACTIVITY_INTENT";
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private CustomProgressDialog progressDialog;
    private boolean checkDeviceSettingsByOnResume = true;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        saveActivityIntent();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected void saveActivityIntent() {
        PreferencesManager preferences = PreferencesManager.getInstance();
        preferences.setString(KEY_SAVED_ACTIVITY_INTENT, getIntent().toUri(0));
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
    }

    public void checkDeviceSettingsByOnResume(boolean check) {
        this.checkDeviceSettingsByOnResume = check;
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
        hideLoading();
        progressDialog = CustomProgressDialog.show(this);
        progressDialog.setCancelable(isCancelable);
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected void hideActionBar() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();
    }

    protected void setHomeAsUp() {
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
