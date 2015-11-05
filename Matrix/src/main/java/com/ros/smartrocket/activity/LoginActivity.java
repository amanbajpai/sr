package com.ros.smartrocket.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.helpers.WriteDataHelper;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Activity for Agents login into system
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {

    public static String START_PUSH_NOTIFICATIONS_ACTIVITY = "start_push_notif";

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private TextView currentVersion;
    private Button loginButton;
    private Button registerButton;
    private CustomProgressDialog progressDialog;

    public LoginActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.red));

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        currentVersion = (TextView) findViewById(R.id.currentVersion);

        currentVersion.setText("v." + BuildConfig.LOGIN_SCREEN_VERSION);

        String lastEmail = preferencesManager.getLastEmail();
        String lastPassword = preferencesManager.getLastPassword();

        if (!TextUtils.isEmpty(lastEmail) || !TextUtils.isEmpty(lastPassword)) {
            emailEditText.setText(lastEmail);
            passwordEditText.setText(lastPassword);
            rememberMeCheckBox.setChecked(true);
        }

        findViewById(R.id.forgotPasswordButton).setOnClickListener(this);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        setSupportProgressBarIndeterminateVisibility(false);

        checkDeviceSettingsByOnResume(false);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                //LoginResponse loginResponse = (LoginResponse) operation.getResponseEntities().get(0);

                String email = emailEditText.getText().toString().trim();
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
                if (!getIntent().getBooleanExtra(START_PUSH_NOTIFICATIONS_ACTIVITY, false)) {
                    startActivity(new Intent(this, MainActivity.class));
                }
            } else if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
                CheckLocationResponse checkLocationResponse =
                        (CheckLocationResponse) operation.getResponseEntities().get(0);

                if (checkLocationResponse.getStatus()) {
                    Intent intent = new Intent(this, PromoCodeActivity.class);
                    intent.putExtra(Keys.DISTRICT_ID, checkLocationResponse.getDistrictId());
                    intent.putExtra(Keys.COUNTRY_ID, checkLocationResponse.getCountryId());
                    intent.putExtra(Keys.CITY_ID, checkLocationResponse.getCityId());
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, CheckLocationActivity.class));
                }
                registerButton.setEnabled(true);
                dismissProgressDialog();
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
            } else if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
                registerButton.setEnabled(true);
                dismissProgressDialog();
                if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode()
                        == BaseNetworkService.NO_INTERNET) {
                    DialogUtils.showBadOrNoInternetDialog(this);
                } else {
                    startActivity(new Intent(this, CheckLocationActivity.class));
                }
            }
        }
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    if (deviceIsReady()) {
                        if (isAllFilesSend(email)) {
                            progressDialog = CustomProgressDialog.show(this);
                            loginButton.setEnabled(false);

                            String deviceManufacturer = UIUtils.getDeviceManufacturer();
                            String deviceModel = UIUtils.getDeviceModel();
                            String deviceName = UIUtils.getDeviceName(this);

                            apiFacade.login(this, email, password, deviceName, deviceModel,
                                    deviceManufacturer, UIUtils.getAppVersion(this),
                                    Build.VERSION.RELEASE);
                        } else {
                            // not all tasks are sent - cannot login
                            DialogUtils.showNotAllFilesSendDialog(this);
                        }
                    }
                } else {
                    UIUtils.showSimpleToast(this, R.string.fill_in_field);
                }

                break;
            case R.id.registerButton:
                if (deviceIsReady()) {
                    progressDialog = CustomProgressDialog.show(this);
                    progressDialog.setCancelable(false);
                    registerButton.setEnabled(false);
                    setSupportProgressBarIndeterminateVisibility(true);

                    MatrixLocationManager.getAddressByCurrentLocation(false,
                            new MatrixLocationManager.GetAddressListener() {
                                @Override
                                public void onGetAddressSuccess(Location location,
                                                                String countryName,
                                                                String cityName,
                                                                String districtName) {
                                    apiFacade.checkLocationForRegistration(LoginActivity.this,
                                            countryName, cityName, districtName,
                                            location.getLatitude(), location.getLongitude());

                                }
                            });
                }
                break;
            case R.id.forgotPasswordButton:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    private boolean isAllFilesSend(String currentEmail) {
        boolean result = true;
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        String lastEmail = preferencesManager.getLastEmail();
        if (!lastEmail.equals(currentEmail)) {
            int notUploadedFileCount = FilesBL.getNotUploadedFileCount();
            result = notUploadedFileCount == 0;
        }
        return result;
    }

    public boolean deviceIsReady() {
        boolean result = UIUtils.isOnline(this) && UIUtils.isAllLocationSourceEnabled(this)
                && !UIUtils.isMockLocationEnabled(this);
        if (!UIUtils.isOnline(this)) {
            DialogUtils.showNetworkDialog(this);
        } else if (!UIUtils.isAllLocationSourceEnabled(this)) {
            DialogUtils.showLocationDialog(this, true);
        } else if (UIUtils.isMockLocationEnabled(this)) {
            DialogUtils.showMockLocationDialog(this, true);
        }

        return result;
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
}
