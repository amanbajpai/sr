package com.ros.smartrocket.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Activity for Agents login into system
 */
public class ForgotPasswordSuccessActivity extends BaseActivity implements View.OnClickListener {
    private String email = "";

    public ForgotPasswordSuccessActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password_success);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.orange));

        if (getIntent() != null) {
            email = getIntent().getStringExtra(Keys.EMAIL);
        }

        ((TextView) findViewById(R.id.email)).setText(email);

        findViewById(R.id.okButton).setOnClickListener(this);

        checkDeviceSettingsByOnResume(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.okButton:
                finish();
                break;
            default:
                break;
        }
    }
}
