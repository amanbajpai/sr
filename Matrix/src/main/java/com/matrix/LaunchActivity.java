package com.matrix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.matrix.activity.LoginActivity;
import com.matrix.net.NetworkService;
import com.matrix.utils.PreferencesManager;

public class LaunchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = null;
        if (!PreferencesManager.getInstance().getToken().equals("")) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        finish();
    }

}
