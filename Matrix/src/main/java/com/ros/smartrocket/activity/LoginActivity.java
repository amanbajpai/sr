package com.ros.smartrocket.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import com.google.android.gms.common.ConnectionResult;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.LoginBL;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.LoginResponse;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.net.gcm.CommonUtilities;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.GoogleUrlShortenManager;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import static com.google.android.gms.common.GooglePlayServicesUtil.getErrorDialog;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;
import static com.google.android.gms.common.GooglePlayServicesUtil.isUserRecoverableError;

/**
 * Activity for Agents login into system
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleUrlShortenManager googleUrlShortenManager = GoogleUrlShortenManager.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private Button registerButton;
    private Location currentLocation;
    private Address currentAddress;
    private CustomProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.red));

        final MatrixLocationManager lm = new MatrixLocationManager(getApplicationContext());
        Location loc = lm.getLocation();
        L.i(TAG, "[LOC = " + loc + "]");
        lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
            @Override
            public void onUpdate(Location location) {
                L.i(TAG, "[NEW LOC = " + location + "]");
            }
        });

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        rememberMeCheckBox = (CheckBox) findViewById(R.id.rememberMeCheckBox);

        String lastEmail = preferencesManager.getLastEmail();
        String lastPassword = preferencesManager.getLastPassword();

        /*if (Config.ENV == Config.Environment.DEVELOPMENT) {
            emailEditText.setText(getString(R.string.login_test_account));
            passwordEditText.setText(getString(R.string.login_test_password));
        } else*/
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
                LoginResponse loginResponse = (LoginResponse) operation.getResponseEntities().get(0);

                //Generate Short url to share
                googleUrlShortenManager.getShortUrl(this, loginResponse.getSharedLink(),
                        new GoogleUrlShortenManager.OnShotrUrlReadyListener() {
                            @Override
                            public void onShortUrlReady(String shortUrl) {
                                preferencesManager.setShortUrlToShare(shortUrl);
                            }

                            @Override
                            public void onGetShortUrlError(String errorString) {

                            }
                        }
                );

                // Check if we are registered on Server side our GCM Id
                /*if (!preferencesManager.isGCMIdRegisteredOnServer()) {
                    String regId = preferencesManager.getGCMRegistrationId();
                    if ("".equals(regId)) {*/
                CommonUtilities.registerGCMInBackground();
                    /*} else {
                        APIFacade.getInstance().registerGCMId(App.getInstance(), regId);
                    }
                }*/

                if (rememberMeCheckBox.isChecked()) {
                    preferencesManager.setLastEmail(emailEditText.getText().toString().trim());
                    preferencesManager.setLastPassword(passwordEditText.getText().toString().trim());
                } else {
                    preferencesManager.setLastEmail("");
                    preferencesManager.setLastPassword("");
                }
                progressDialog.dismiss();
                finish();
                startActivity(new Intent(this, MainActivity.class));
            } else if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
                CheckLocationResponse checkLocationResponse =
                        (CheckLocationResponse) operation.getResponseEntities().get(0);

                if (checkLocationResponse.getStatus()) {
                    Intent intent = new Intent(this, ReferralCasesActivity.class);
                    intent.putExtra(Keys.COUNTRY_ID, checkLocationResponse.getCountryId());
                    intent.putExtra(Keys.COUNTRY_NAME, currentAddress.getCountryName());
                    intent.putExtra(Keys.CITY_ID, checkLocationResponse.getCityId());
                    intent.putExtra(Keys.CITY_NAME, currentAddress.getLocality());
                    intent.putExtra(Keys.LATITUDE, currentLocation.getLatitude());
                    intent.putExtra(Keys.LONGITUDE, currentLocation.getLongitude());
                    startActivity(intent);
                } else {
                    startActivity(new Intent(this, CheckLocationActivity.class));
                }
                registerButton.setEnabled(true);
                progressDialog.dismiss();
            }
        } else if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode() == BaseNetworkService
                .ACCOUNT_NOT_ACTIVATED_ERROR_CODE) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.dismiss();
                loginButton.setEnabled(true);
                DialogUtils.showAccountNotActivatedDialog(this);
            }
        } else if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode() == BaseNetworkService
                .NO_INTERNET) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.dismiss();
                loginButton.setEnabled(true);
                DialogUtils.showBadOrNoInternetDialog(this);
            }
        } else {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.dismiss();
                loginButton.setEnabled(true);
                DialogUtils.showLoginFailedDialog(this);
            } else if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
                startActivity(new Intent(this, CheckLocationActivity.class));
                registerButton.setEnabled(true);
                progressDialog.dismiss();
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                /*String result1 = "";
                String result2 = "";
                String result3 = "";
                try {
                    result1 = new String(email.getBytes("UTF-8"));
                    result2 = new String(email.getBytes("UTF-8"), "UTF-8");
                    result3 = new String(email.getBytes("UTF-16"), "UTF-16");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                L.e(TAG, "Result 1: " + result1);
                L.e(TAG, "Result 2: " + result2);
                L.e(TAG, "Result 3: " + result3);*/

                LoginBL lBL = new LoginBL();
                LoginBL.PreLoginErrors error = lBL.login(this, email, password);

                if (error == LoginBL.PreLoginErrors.SUCCESS) {
                    progressDialog = CustomProgressDialog.show(this);
                    loginButton.setEnabled(false);
                    apiFacade.login(this, email, password);
                } else if (error == LoginBL.PreLoginErrors.NOCONNECTION) {
                    DialogUtils.showNetworkDialog(this);
                } else if (error == LoginBL.PreLoginErrors.GPSOFF) {
                    DialogUtils.showLocationDialog(this, true);
                } else if (error == LoginBL.PreLoginErrors.GOOGLEPSNOTWALID) {
                    DialogUtils.showGoogleSdkDialog(this);
                } else if (error == LoginBL.PreLoginErrors.MOCKON) {
                    DialogUtils.showMockLocationDialog(this, true);
                } else if (error == LoginBL.PreLoginErrors.NOPASSWORDOREMAIL) {
                    DialogUtils.showLoginFailedDialog(this);
                }

                break;
            case R.id.registerButton:
                if (!UIUtils.isOnline(this)) {
                    DialogUtils.showNetworkDialog(this);
                } else if (!UIUtils.isGpsEnabled(this)) {
                    DialogUtils.showLocationDialog(this, true);
                } else if (!UIUtils.isGooglePlayServicesEnabled(this)) {
                    DialogUtils.showGoogleSdkDialog(this);
                } else if (UIUtils.isMockLocationEnabled(this)) {
                    DialogUtils.showMockLocationDialog(this, true);
                } else {
                    progressDialog = CustomProgressDialog.show(this);
                    registerButton.setEnabled(false);
                    setSupportProgressBarIndeterminateVisibility(true);

                    Location location = lm.getLocation();
                    if (location != null) {
                        getAddressByLocation(location);
                    } else {
                        lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                            @Override
                            public void onUpdate(Location location) {
                                L.i(TAG, "Location Updated!");
                                getAddressByLocation(location);
                            }
                        });
                    }
                }
                break;
            case R.id.forgotPasswordButton:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    public void getAddressByLocation(Location location) {
        this.currentLocation = location;
        lm.getAddress(location, new MatrixLocationManager.IAddress() {
            @Override
            public void onUpdate(Address address) {
                if (address != null) {
                    L.e(TAG, "CountryCode " + address.getCountryCode());
                    currentAddress = address;
                    apiFacade.checkLocationForRegistration(LoginActivity.this,
                            address.getCountryName(), address.getLocality(),
                            address.getLatitude(), address.getLongitude());
                } else {
                    startActivity(new Intent(LoginActivity.this, CheckLocationActivity.class));
                }
            }
        });
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean isPlayServicesAvailable(Context context) {
        int resultCode = isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (isUserRecoverableError(resultCode)) {
                getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                L.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
