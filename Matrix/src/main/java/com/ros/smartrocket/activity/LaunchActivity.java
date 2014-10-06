package com.ros.smartrocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ros.smartrocket.utils.PreferencesManager;

public class LaunchActivity extends Activity {

    public LaunchActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent;
        if (!TextUtils.isEmpty(PreferencesManager.getInstance().getToken())) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
