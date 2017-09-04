package com.ros.smartrocket.flow.login.password;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.activity.MainActivity;
import com.ros.smartrocket.flow.login.password.forgot.ForgotPasswordActivity;
import com.ros.smartrocket.flow.login.terms.TermsAndConditionActivity;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.flow.login.LoginActivity;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomCheckBox;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends BaseActivity implements PasswordMvpView {
    public static String EMAIL = "email";

    @BindView(R.id.passwordEditText)
    CustomEditTextView passwordEditText;
    @BindView(R.id.rememberMeCheckBox)
    CustomCheckBox rememberMeCheckBox;
    @BindView(R.id.login_btn)
    CustomButton loginButton;

    private PasswordMvpPresenter<PasswordMvpView> presenter;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private String email;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        initUI();
        presenter = new PasswordPresenter<>();
        presenter.attachView(this);
    }

    private void initUI() {
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.red));

        if (getIntent().getExtras() != null) email = getIntent().getExtras().getString(EMAIL);

        String lastPassword = preferencesManager.getLastPassword();
        if (!TextUtils.isEmpty(lastPassword)) {
            passwordEditText.setText(lastPassword);
            rememberMeCheckBox.setChecked(true);
        }
    }

    @OnClick({R.id.login_btn, R.id.forgotPasswordButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.forgotPasswordButton:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }

    private void login() {
        String password = passwordEditText.getText().toString().trim();
        if (UIUtils.isDeviceReady(this)) presenter.login(email, password);
    }

    @Override
    public void onLoginSuccess(LoginResponse response) {
        preferencesManager.setLastAppVersion(UIUtils.getAppVersionCode(this));
        if (response.isShowTermsConditions()) {
            Intent intent = new Intent(this, TermsAndConditionActivity.class);
            intent.putExtra(Keys.SHOULD_SHOW_MAIN_SCREEN, true);
            startActivity(intent);
        } else if (!getIntent().getBooleanExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, false)) {
            preferencesManager.setTandCShowedForCurrentUser();
            startActivity(new Intent(this, MainActivity.class));
        }
        sendBroadcast(new Intent().setAction(Keys.FINISH_LOGIN_ACTIVITY));
        finish();
    }

    @Override
    public boolean shouldStorePassword() {
        return rememberMeCheckBox.isChecked();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        loginButton.setEnabled(true);
        switch (networkError.getErrorCode()) {
            case NetworkError.ACCOUNT_NOT_ACTIVATED_ERROR_CODE:
                DialogUtils.showAccountNotActivatedDialog(this);
                break;
            case NetworkError.NO_INTERNET:
                DialogUtils.showBadOrNoInternetDialog(this);
                break;
            case NetworkError.USER_NOT_FOUND_ERROR_CODE:
                DialogUtils.showLoginFailedDialog(this);
                break;
            default:
                UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
                break;
        }
    }

    @Override
    public void onPasswordFieldEmpty() {
        UIUtils.showSimpleToast(this, R.string.fill_in_field);
    }

    @Override
    public Login getLoginEntity(String email, String password) {
        Login loginEntity = new Login();
        String deviceManufacturer = UIUtils.getDeviceManufacturer();
        String deviceModel = UIUtils.getDeviceModel();
        String deviceName = UIUtils.getDeviceName(this);
        loginEntity.setEmail(email);
        loginEntity.setPassword(password);
        loginEntity.setDeviceName(deviceName);
        loginEntity.setDeviceModel(deviceModel);
        loginEntity.setDeviceManufacturer(deviceManufacturer);
        loginEntity.setAppVersion(UIUtils.getAppVersion(this));
        loginEntity.setAndroidVersion(Build.VERSION.RELEASE);

        Intent intent = getIntent();
        if (intent != null) {
            loginEntity.setCityId(intent.getIntExtra(Keys.CITY_ID, 0));
            loginEntity.setCountryId(intent.getIntExtra(Keys.COUNTRY_ID, 0));
            loginEntity.setDistrictId(intent.getIntExtra(Keys.DISTRICT_ID, 0));
            loginEntity.setLongitude(intent.getDoubleExtra(Keys.LONGITUDE, 0));
            loginEntity.setLatitude(intent.getDoubleExtra(Keys.LATITUDE, 0));
        }
        return loginEntity;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
