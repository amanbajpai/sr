package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.dialog.CustomProgressDialog;
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
import com.ros.smartrocket.views.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PasswordActivity extends BaseActivity implements NetworkOperationListenerInterface {
    public static String EMAIL = "email";
    @Bind(R.id.passwordEditText)
    CustomEditTextView passwordEditText;
    @Bind(R.id.rememberMeCheckBox)
    CustomCheckBox rememberMeCheckBox;
    @Bind(R.id.continue_with_email_btn)
    CustomButton loginButton;
    @Bind(R.id.forgotPasswordButton)
    CustomTextView forgotPasswordButton;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;
    private String email;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                //LoginResponse loginResponse = (LoginResponse) operation.getResponseEntities().get(0);
                String password = passwordEditText.getText().toString().trim();

                WriteDataHelper.prepareLogin(this, email);

                preferencesManager.setLastAppVersion(UIUtils.getAppVersionCode(this));
                preferencesManager.setLastEmail(email);
                if (rememberMeCheckBox.isChecked()) {
                    preferencesManager.setLastPassword(password);
                } else {
                    preferencesManager.setLastPassword("");
                }
                dismissProgressDialog();
                finish();
                sendBroadcast(new Intent().setAction(Keys.FINISH_LOGIN_ACTIVITY));
                if (!getIntent().getBooleanExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, false)) {
                    startActivity(new Intent(this, MainActivity.class));
                }
            }
        } else {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressDialog();
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
                    UIUtils.showSimpleToast(this, operation.getResponseError(), Toast.LENGTH_LONG, Gravity.BOTTOM);
                }
            }
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @OnClick({R.id.continue_with_email_btn, R.id.forgotPasswordButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.continue_with_email_btn:
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
                progressDialog = CustomProgressDialog.show(this);
                loginButton.setEnabled(false);
                String deviceManufacturer = UIUtils.getDeviceManufacturer();
                String deviceModel = UIUtils.getDeviceModel();
                String deviceName = UIUtils.getDeviceName(this);
                apiFacade.login(this, email, password, deviceName, deviceModel,
                        deviceManufacturer, UIUtils.getAppVersion(this),
                        Build.VERSION.RELEASE);
            }
        } else {
            UIUtils.showSimpleToast(this, R.string.fill_in_field);
        }
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
