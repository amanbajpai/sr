package com.ros.smartrocket.activity;

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
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.helpers.WriteDataHelper;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomCheckBox;
import com.ros.smartrocket.views.CustomEditTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends BaseActivity implements NetworkOperationListenerInterface {
    public static String EMAIL = "email";
    @BindView(R.id.passwordEditText)
    CustomEditTextView passwordEditText;
    @BindView(R.id.rememberMeCheckBox)
    CustomCheckBox rememberMeCheckBox;
    @BindView(R.id.login_btn)
    CustomButton loginButton;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private String email;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.red));
        if (getIntent().getExtras() != null) {
            email = getIntent().getExtras().getString(EMAIL);
        }
        String lastPassword = preferencesManager.getLastPassword();
        if (!TextUtils.isEmpty(lastPassword)) {
            passwordEditText.setText(lastPassword);
            rememberMeCheckBox.setChecked(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    protected void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        dismissProgressDialog();
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                LoginResponse loginResponse = (LoginResponse) operation.getResponseEntities().get(0);

                String password = passwordEditText.getText().toString().trim();
                WriteDataHelper.prepareLogin(this, email);

                preferencesManager.setLastAppVersion(UIUtils.getAppVersionCode(this));
                preferencesManager.setLastEmail(email);
                if (rememberMeCheckBox.isChecked()) {
                    preferencesManager.setLastPassword(password);
                } else {
                    preferencesManager.setLastPassword("");
                }
                finish();
                if (loginResponse.isShowTermsConditions()) {
                    Intent intent = new Intent(this, TermsAndConditionActivity.class);
                    intent.putExtra(Keys.SHOULD_SHOW_MAIN_SCREEN, true);
                    startActivity(intent);
                } else if (!getIntent().getBooleanExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, false)) {
                    preferencesManager.setTandCShowedForCurrentUser();
                    startActivity(new Intent(this, MainActivity.class));
                }
                sendBroadcast(new Intent().setAction(Keys.FINISH_LOGIN_ACTIVITY));
            }
        } else {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                loginButton.setEnabled(true);
                if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode()
                        == BaseNetworkService.ACCOUNT_NOT_ACTIVATED_ERROR_CODE) {
                    DialogUtils.showAccountNotActivatedDialog(this);

                } else if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode()
                        == BaseNetworkService.NO_INTERNET) {
                    DialogUtils.showBadOrNoInternetDialog(this);

                } else if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode()
                        == BaseNetworkService.USER_NOT_FOUND_ERROR_CODE) {
                    DialogUtils.showLoginFailedDialog(this);

                } else {
                    showNetworkError(operation);
                }
            }
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
        if (!TextUtils.isEmpty(password)) {
            if (UIUtils.deviceIsReady(this)) {
                login(email, password);
            }
        } else {
            UIUtils.showSimpleToast(this, R.string.fill_in_field);
        }
    }

    private void login(String email, String password) {
        showProgressDialog(false);
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
        apiFacade.login(this, loginEntity);
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
}
