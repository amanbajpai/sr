package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.Login;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.dialog.CheckLocationDialog;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.TutorialView;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomEditTextView;
import com.ros.smartrocket.views.CustomTextView;
import com.ros.smartrocket.views.SocialLoginView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity for Agents login into system
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, PopupMenu.OnMenuItemClickListener {

    public static String START_PUSH_NOTIFICATIONS_ACTIVITY = "start_push_notif";
    @Bind(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @Bind(R.id.continue_with_email_btn)
    CustomButton continueWithEmailBtn;
    @Bind(R.id.currentVersion)
    CustomTextView currentVersion;
    @Bind(R.id.social_login_view)
    SocialLoginView socialLoginView;
    @Bind(R.id.language)
    CustomTextView language;

    private boolean startPushNotificationActivity;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;
    private RegistrationPermissions registrationPermissions;
    private CheckLocationDialog checkLocationDialog;
    private CheckLocationResponse checkLocationResponse;
    double latitude;
    double longitude;
    private LogOutReceiver localReceiver;

    public LoginActivity() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialLoginView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.red));
        startPushNotificationActivity = getIntent().getBooleanExtra(START_PUSH_NOTIFICATIONS_ACTIVITY, false);
        currentVersion.setText("v." + BuildConfig.LOGIN_SCREEN_VERSION);
        socialLoginView.setUpSocialLogins(this);
        fillLanguageTv();
        String lastEmail = preferencesManager.getLastEmail();

        if (!TextUtils.isEmpty(lastEmail)) {
            emailEditText.setText(lastEmail);
        }
        continueWithEmailBtn.setOnClickListener(this);
        checkDeviceSettingsByOnResume(false);
        localReceiver = new LogOutReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.FINISH_LOGIN_ACTIVITY);

        registerReceiver(localReceiver, intentFilter);
    }

    private void fillLanguageTv() {
        String currentLanguageCode = preferencesManager.getLanguageCode();
        if (TextUtils.isEmpty(currentLanguageCode)) {
            currentLanguageCode = UIUtils.DEFAULT_LANG;
        }
        int selectedLangPosition = 0;
        for (int i = 0; i < UIUtils.VISIBLE_LANGS_CODE.length; i++) {
            if (UIUtils.VISIBLE_LANGS_CODE[i].equals(currentLanguageCode)) {
                selectedLangPosition = i;
                break;
            }
        }
        switch (selectedLangPosition) {
            case 0:
                language.setText(R.string.english);
                break;
            case 1:
                language.setText(R.string.chinese_simple);
                break;
            case 2:
                language.setText(R.string.chinese_traditional);
                break;
        }

    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
                CheckLocationResponse checkLocationResponse =
                        (CheckLocationResponse) operation.getResponseEntities().get(0);

                if (checkLocationResponse.getStatus()) {
                    Intent intent = new Intent(this, PromoCodeActivity.class);
                    intent.putExtra(Keys.DISTRICT_ID, checkLocationResponse.getDistrictId());
                    intent.putExtra(Keys.COUNTRY_ID, checkLocationResponse.getCountryId());
                    intent.putExtra(Keys.CITY_ID, checkLocationResponse.getCityId());
                }
            } else {
                if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                    dismissProgressDialog();
                    //loginButton.setEnabled(true);
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
                    // TODO registerButton.setEnabled(true);
                    dismissProgressDialog();
                } else if (Keys.CHECK_LOCATION_OPERATION_TAG.equals(operation.getTag())) {
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
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_with_email_btn:
                String email = emailEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {
                    if (UIUtils.deviceIsReady(this)) {
                        if (UIUtils.isAllFilesSend(email)) {
                            progressDialog = CustomProgressDialog.show(this);
                            // TODO check email
                            progressDialog.dismiss();
                            Intent i = new Intent(this, PasswordActivity.class);
                            i.putExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, startPushNotificationActivity);
                            i.putExtra(PasswordActivity.EMAIL, email);
                            startActivity(i);
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
                    startRegistrationFlow();
                }
                break;
            case R.id.forgotPasswordButton:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    private void login(String email, String password) {
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
        if (checkLocationResponse != null) {
            loginEntity.setCityId(checkLocationResponse.getCityId());
            loginEntity.setCountryId(checkLocationResponse.getCountryId());
            loginEntity.setDistrictId(checkLocationResponse.getDistrictId());
            loginEntity.setLongitude(longitude);
            loginEntity.setLatitude(latitude);
        }
        apiFacade.login(this, loginEntity);
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
    protected void onResume() {
        super.onResume();
        if (deviceIsReady() && checkLocationDialog == null) {
            getLocation();
        }
    }

    @Override
    protected void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }

    private void getLocation() {
        checkLocationDialog = new CheckLocationDialog(this,
                new CheckLocationDialog.CheckLocationListener() {
                    @Override
                    public void onLocationChecked(Dialog dialog, String countryName, String cityName,
                                                  double latitude, double longitude,
                                                  CheckLocationResponse serverResponse) {
                        LoginActivity.this.onLocationChecked(serverResponse, latitude, longitude);
                        // TODO location success
                    }

                    @Override
                    public void onCheckLocationFailed(Dialog dialog, String countryName, String cityName,
                                                      double latitude, double longitude,
                                                      CheckLocationResponse serverResponse) {
                        LoginActivity.this.onLocationChecked(serverResponse, latitude, longitude);
                        // TODO location failed
                    }
                }
                , true);
    }

    private void onLocationChecked(CheckLocationResponse serverResponse, double latitude,
                                   double longitude) {
        if (serverResponse != null) {
            checkLocationResponse = serverResponse;
            this.latitude = latitude;
            this.longitude = longitude;
            registrationPermissions = checkLocationResponse.getRegistrationPermissions();
            PreferencesManager.getInstance().saveRegistrationPermissions(registrationPermissions);
        }
        continueWithEmailBtn.setEnabled(true);
    }

    private void startRegistrationFlow() {
        if (checkLocationResponse != null && checkLocationResponse.getStatus()) {
            Intent intent;
            registrationPermissions = PreferencesManager.getInstance().getRegPermissions();
            if (registrationPermissions.isSlidersEnable()) {
                intent = new Intent(this, TutorialActivity.class);
            } else if (registrationPermissions.isTermsEnable()) {
                intent = new Intent(this, TermsAndConditionActivity.class);
            } else if (registrationPermissions.isReferralEnable()) {
                intent = new Intent(this, ReferralCasesActivity.class);
            } else if (registrationPermissions.isSrCodeEnable()) {
                intent = new Intent(this, PromoCodeActivity.class);
            } else {
                intent = new Intent(this, RegistrationActivity.class);
            }
            intent.putExtra(Keys.COUNTRY_ID, checkLocationResponse.getCountryId());
            intent.putExtra(Keys.CITY_ID, checkLocationResponse.getCityId());
            intent.putExtra(Keys.DISTRICT_ID, checkLocationResponse.getDistrictId());
            intent.putExtra(Keys.COUNTRY_NAME, checkLocationResponse.getCountryName());
            intent.putExtra(Keys.CITY_NAME, checkLocationResponse.getCityName());
            intent.putExtra(Keys.LATITUDE, latitude);
            intent.putExtra(Keys.LONGITUDE, longitude);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, CheckLocationActivity.class));
        }
    }

    @OnClick(R.id.language)
    public void onClick() {
        showPopup(language);
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.menu_language);
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lanChineseSimple:
                onLanguageChanged("zh_CN");
                language.setText(R.string.chinese_simple);
                return true;
            case R.id.lanChineseTraditional:
                onLanguageChanged("zh_TW");
                language.setText(R.string.chinese_traditional);
                return true;
            case R.id.lanEnglish:
                onLanguageChanged("en");
                language.setText(R.string.english);
                return true;
            default:
                return false;
        }
    }

    private void onLanguageChanged(String code) {
        boolean languageChanged = UIUtils.setDefaultLanguage(this, code);
        if (languageChanged) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    public class LogOutReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Keys.FINISH_LOGIN_ACTIVITY)) {
                finish();
            }
        }
    }
}
