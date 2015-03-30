package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import cn.jpush.android.api.InstrumentedActivity;

public class LaunchActivity extends InstrumentedActivity {
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    public LaunchActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        Intent intent;
        if (!TextUtils.isEmpty(PreferencesManager.getInstance().getToken())
                && preferencesManager.getLastAppVersion() == UIUtils.getAppVersionCode(this)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
