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
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckEmail;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.ExternalAuthResponse;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.db.entity.ResponseError;
import com.ros.smartrocket.dialog.CheckLocationDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.interfaces.SocialLoginListener;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;
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
public class LoginActivity extends BaseActivity implements NetworkOperationListenerInterface,
        PopupMenu.OnMenuItemClickListener, SocialLoginListener {

    public static String START_PUSH_NOTIFICATIONS_ACTIVITY = "start_push_notif";
    @Bind(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @Bind(R.id.continue_btn)
    CustomButton continueWithEmailBtn;
    @Bind(R.id.currentVersion)
    CustomTextView currentVersion;
    @Bind(R.id.social_login_view)
    SocialLoginView socialLoginView;
    @Bind(R.id.language)
    CustomTextView language;

    private boolean startPushNotificationActivity;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private RegistrationPermissions registrationPermissions;
    private CheckLocationDialog checkLocationDialog;
    private CheckLocationResponse checkLocationResponse;
    double latitude;
    double longitude;
    private APIFacade apiFacade = APIFacade.getInstance();
    private String userEmail;
    private ExternalAuthorize authorize;
    private int registrationBitMask;
    private LoginActivityReceiver localReceiver;

    public LoginActivity() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialLoginView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.red));
        startPushNotificationActivity = getIntent().getBooleanExtra(START_PUSH_NOTIFICATIONS_ACTIVITY, false);
        currentVersion.setText("v." + BuildConfig.VERSION_NAME);
        fillLanguageTv();
        String lastEmail = preferencesManager.getLastEmail();

        if (!TextUtils.isEmpty(lastEmail)) {
            emailEditText.setText(lastEmail);
        }
        continueWithEmailBtn.setEnabled(false);
        checkDeviceSettingsByOnResume(false);
        localReceiver = new LoginActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.FINISH_LOGIN_ACTIVITY);
        intentFilter.addAction(Keys.WECHAT_AUTH_SUCCESS);
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
            case 3:
                language.setText(R.string.french);
                break;
        }

    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        dismissProgressDialog();
        if (checkLocationDialog != null) {
            checkLocationDialog.onNetworkOperation(operation);
        }
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            onSuccessNetworkOperation(operation);
        } else {
            onErrorNetworkOperation(operation);
        }
    }


    private void onSuccessNetworkOperation(BaseOperation operation) {
        if (Keys.GET_CHECK_EMAIL_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            CheckEmail checkEmail = (CheckEmail) operation.getResponseEntities().get(0);
            if (checkEmail.isEmailExists()) {
                startPasswordActivity(userEmail);
            } else {
                startRegistrationFlow(RegistrationType.NORMAL);
            }
        } else if (Keys.POST_EXTERNAL_AUTH_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            ExternalAuthResponse externalAuthResponse = (ExternalAuthResponse) operation.getResponseEntities().get(0);
            preferencesManager.setLastAppVersion(UIUtils.getAppVersionCode(this));
            if (authorize != null) {
                preferencesManager.setLastEmail(authorize.getEmail());
            }
            if (externalAuthResponse.isRegistrationRequested()) {
                startRegistrationFlow(RegistrationType.SOCIAL);
            } else if (externalAuthResponse.isShowTermsConditions()) {
                Intent intent = new Intent(this, TermsAndConditionActivity.class);
                intent.putExtra(Keys.SHOULD_SHOW_MAIN_SCREEN, true);
                startActivity(intent);
            } else {
                startActivity(new Intent(this, MainActivity.class));
                preferencesManager.setTandCShowedForCurrentUser();
            }
            finish();
        }
    }

    private void onErrorNetworkOperation(BaseOperation operation) {
        if (operation.getResponseErrorCode() != null) {
            if (operation.getResponseErrorCode() == BaseNetworkService.NO_INTERNET) {
                DialogUtils.showBadOrNoInternetDialog(this);
            } else if (operation.getResponseErrorCode() == BaseNetworkService.EXTERNAL_AUTH_NEED_MORE_DATA_ERROR) {
                Gson gson = new Gson();
                ResponseError error = gson.fromJson(operation.getResponseString(), ResponseError.class);
                if (error != null && error.getData() != null) {
                    registrationBitMask = error.getData().getMissingFields();
                }
                startRegistrationFlow(RegistrationType.SOCIAL_ADDITIONAL_INFO);
            } else if (operation.getResponseErrorCode() == BaseNetworkService.ACCOUNT_NOT_ACTIVATED_ERROR_CODE
                    || operation.getResponseErrorCode() == BaseNetworkService.EMAIL_SENT_ERROR) {
                DialogUtils.showAccountNotActivatedDialog(this);
            } else {
                showNetworkError(operation);
            }
        } else {
            showNetworkError(operation);
        }
    }

    public boolean deviceIsReady() {
        boolean result = UIUtils.isOnline(this) && UIUtils.isAllLocationSourceEnabled(this)
                && !UIUtils.isMockLocationEnabled(this, App.getInstance().getLocationManager().getLocation());
        if (!UIUtils.isOnline(this)) {
            DialogUtils.showNetworkDialog(this);
        } else if (!UIUtils.isAllLocationSourceEnabled(this)) {
            DialogUtils.showLocationDialog(this, true);
        } else if (UIUtils.isMockLocationEnabled(this, App.getInstance().getLocationManager().getLocation())) {
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

    private void onLocationChecked(CheckLocationResponse serverResponse, double latitude, double longitude) {
        checkLocationDialog = null;
        if (serverResponse != null) {
            checkLocationResponse = serverResponse;
            this.latitude = latitude;
            this.longitude = longitude;
            registrationPermissions = checkLocationResponse.getRegistrationPermissions();
            if (registrationPermissions != null) {
                PreferencesManager.getInstance().saveRegistrationPermissions(registrationPermissions);
                if (registrationPermissions.isSocialEnable()) {
                    socialLoginView.setVisibility(View.VISIBLE);
                    socialLoginView.setUpSocialLoginButtons(this, this,
                            checkLocationResponse.getExternalLoginSource1(), checkLocationResponse.getExternalLoginSource2());
                } else {
                    socialLoginView.setVisibility(View.GONE);
                }
            }
        }
        continueWithEmailBtn.setEnabled(true);
    }

    private void startRegistrationFlow(RegistrationType type) {
        UIUtils.setCurrentLanguage();
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
            if (type == RegistrationType.SOCIAL_ADDITIONAL_INFO) {
                intent.putExtra(ExternalAuthDetailsActivity.EXTERNAL_AUTHORIZE, authorize);
                intent.putExtra(ExternalAuthDetailsActivity.BITMASK, registrationBitMask);
            }
            if (type == RegistrationType.NORMAL) {
                intent.putExtra(Keys.EMAIL, emailEditText.getText().toString().trim());
            }
            intent.putExtra(Keys.COUNTRY_NAME, checkLocationResponse.getCountryName());
            intent.putExtra(Keys.CITY_NAME, checkLocationResponse.getCityName());
            intent.putExtra(Keys.REGISTRATION_TYPE, type);
            fillIntentWithLocationData(intent);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, CheckLocationActivity.class));
        }
    }

    private void startPasswordActivity(String email) {
        Intent i = new Intent(this, PasswordActivity.class);
        i.putExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, startPushNotificationActivity);
        i.putExtra(PasswordActivity.EMAIL, email);
        fillIntentWithLocationData(i);
        startActivity(i);
    }

    private void fillIntentWithLocationData(Intent intent) {
        if (checkLocationResponse != null && checkLocationResponse.getStatus()) {
            intent.putExtra(Keys.COUNTRY_ID, checkLocationResponse.getCountryId());
            intent.putExtra(Keys.CITY_ID, checkLocationResponse.getCityId());
            intent.putExtra(Keys.DISTRICT_ID, checkLocationResponse.getDistrictId());
            intent.putExtra(Keys.LATITUDE, latitude);
            intent.putExtra(Keys.LONGITUDE, longitude);
        }
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
            case R.id.lanFrance:
                onLanguageChanged("fr");
                language.setText(R.string.french);
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

    @OnClick({R.id.language, R.id.continue_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.language:
                showPopup(language);
                break;
            case R.id.continue_btn:
                userEmail = emailEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(userEmail)) {
                    if (UIUtils.deviceIsReady(this)) {
                        if (UIUtils.isAllFilesSend(userEmail)) {
                            showProgressDialog(false);
                            apiFacade.checkEmail(this, userEmail);
                        } else {
                            // not all tasks are sent - cannot login
                            DialogUtils.showNotAllFilesSendDialog(this);
                        }
                    }
                } else {
                    UIUtils.showSimpleToast(this, R.string.fill_in_field);
                }
                break;
        }
    }

    @Override
    public void onExternalLoginSuccess(ExternalAuthorize authorize) {
        dismissProgressDialog();
        this.authorize = authorize;
        if (checkLocationResponse != null) {
            showProgressDialog(true);
            authorize.setCityId(checkLocationResponse.getCityId());
            authorize.setCountryId(checkLocationResponse.getCountryId());
            authorize.setDistrictId(checkLocationResponse.getDistrictId());
            authorize.setLatitude(latitude);
            authorize.setLongitude(longitude);
            authorize.setDeviceName(UIUtils.getDeviceName(this));
            authorize.setDeviceModel(UIUtils.getDeviceModel());
            authorize.setDeviceManufacturer(UIUtils.getDeviceManufacturer());
            authorize.setAppVersion(UIUtils.getAppVersion(this));
            authorize.setAndroidVersion(Build.VERSION.RELEASE);
            apiFacade.externalAuth(this, authorize);
        }
    }

    @Override
    public void onExternalLoginStart() {
        showProgressDialog(true);
    }

    @Override
    public void onExternalLoginFinished() {
        dismissProgressDialog();
    }

    public class LoginActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Keys.FINISH_LOGIN_ACTIVITY)) {
                finish();
            } else if (action.equals(Keys.WECHAT_AUTH_SUCCESS) && socialLoginView != null) {
                socialLoginView.onActivityResult(SocialLoginView.WECHAT_SIGN_IN_CODE, RESULT_OK, intent);
            }
        }
    }
}
