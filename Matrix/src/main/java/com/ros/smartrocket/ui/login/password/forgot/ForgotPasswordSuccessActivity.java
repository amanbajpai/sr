package com.ros.smartrocket.ui.login.password.forgot;

import android.os.Bundle;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordSuccessActivity extends BaseActivity {
    @BindView(R.id.email)
    CustomTextView email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_success);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) getSupportActionBar().hide();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.orange));

        if (getIntent() != null) email.setText(getIntent().getStringExtra(Keys.EMAIL));
        checkDeviceSettingsByOnResume(false);
    }

    @OnClick(R.id.okButton)
    public void onViewClicked() {
        finish();
    }
}
