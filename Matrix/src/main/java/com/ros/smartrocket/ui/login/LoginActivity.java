package com.ros.smartrocket.ui.login;

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

import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.db.entity.ExternalAuthResponse;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.interfaces.SocialLoginListener;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.activity.MainActivity;
import com.ros.smartrocket.ui.login.password.PasswordActivity;
import com.ros.smartrocket.ui.activity.PromoCodeActivity;
import com.ros.smartrocket.ui.activity.ReferralCasesActivity;
import com.ros.smartrocket.ui.activity.RegistrationActivity;
import com.ros.smartrocket.ui.activity.TermsAndConditionActivity;
import com.ros.smartrocket.ui.activity.TutorialActivity;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.ui.dialog.CheckLocationDialog;
import com.ros.smartrocket.ui.login.location.CheckLocationActivity;
import com.ros.smartrocket.ui.login.location.CheckLocationMvpPresenter;
import com.ros.smartrocket.ui.login.location.CheckLocationMvpView;
import com.ros.smartrocket.ui.login.location.CheckLocationPresenter;
import com.ros.smartrocket.ui.login.external.ExternalAuthDetailsActivity;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.ui.views.SocialLoginView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoginMvpView, CheckLocationMvpView,
        PopupMenu.OnMenuItemClickListener, SocialLoginListener {

    public static String START_PUSH_NOTIFICATIONS_ACTIVITY = "start_push_notif";
    @BindView(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @BindView(R.id.continue_btn)
    CustomButton continueWithEmailBtn;
    @BindView(R.id.currentVersion)
    CustomTextView currentVersion;
    @BindView(R.id.social_login_view)
    SocialLoginView socialLoginView;
    @BindView(R.id.language)
    CustomTextView language;

    private CheckLocationMvpPresenter<CheckLocationMvpView> checkLocationPresenter;
    private LoginMvpPresenter<LoginMvpView> loginPresenter;

    private boolean startPushNotificationActivity;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private RegistrationPermissions registrationPermissions;
    private CheckLocationDialog checkLocationDialog;
    private CheckLocationResponse checkLocationResponse;
    double latitude;
    double longitude;
    private ExternalAuthorize authorize;
    private LoginActivityReceiver localReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initPresenters();
        initUI();
        checkDeviceSettingsByOnResume(false);
        initReceiver();
    }

    private void initUI() {
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.red));
        startPushNotificationActivity = getIntent().getBooleanExtra(START_PUSH_NOTIFICATIONS_ACTIVITY, false);
        currentVersion.setText(getString(R.string.version_var, BuildConfig.VERSION_NAME));
        fillLanguageTv();
        String lastEmail = preferencesManager.getLastEmail();

        if (!TextUtils.isEmpty(lastEmail)) {
            emailEditText.setText(lastEmail);
        }
        continueWithEmailBtn.setEnabled(false);
    }

    private void initReceiver() {
        localReceiver = new LoginActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Keys.FINISH_LOGIN_ACTIVITY);
        intentFilter.addAction(Keys.WECHAT_AUTH_SUCCESS);
        registerReceiver(localReceiver, intentFilter);
    }

    private void initPresenters() {
        checkLocationPresenter = new CheckLocationPresenter<>();
        checkLocationPresenter.attachView(this);
        checkLocationPresenter.checkLocation();
        loginPresenter = new LoginPresenter<>();
        loginPresenter.attachView(this);
    }

    private void fillLanguageTv() {
        String currentLanguageCode = preferencesManager.getLanguageCode();
        if (TextUtils.isEmpty(currentLanguageCode)) {
            currentLanguageCode = LocaleUtils.DEFAULT_LANG;
        }
        int selectedLangPosition = 0;
        for (int i = 0; i < LocaleUtils.VISIBLE_LANGS_CODE.length; i++) {
            if (LocaleUtils.VISIBLE_LANGS_CODE[i].equals(currentLanguageCode)) {
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
                language.setText(R.string.chinese_traditional_hk);
                break;
            case 3:
                language.setText(R.string.chinese_traditional_tw);
                break;
            case 4:
                language.setText(R.string.french);
                break;
            case 5:
                language.setText(R.string.arabic);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UIUtils.isDeviceReady(this) && checkLocationDialog == null && checkLocationResponse == null) {
            checkLocationPresenter.checkLocation();
        }
    }

    public void onLocationChecked(CheckLocationResponse serverResponse, double latitude, double longitude) {
        if (checkLocationDialog != null) {
            checkLocationDialog.checkLocationSuccess();
        }
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

    @Override
    public void onLocationCheckFailed() {
        if (checkLocationDialog != null) {
            checkLocationDialog.checkLocationFail();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        socialLoginView.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showLocationCheckDialog() {
        checkLocationDialog = new CheckLocationDialog(this);
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
            case R.id.lanChineseTraditionalHK:
                onLanguageChanged("zh_HK");
                language.setText(R.string.chinese_traditional_hk);
                return true;
            case R.id.lanChineseTraditionalTW:
                onLanguageChanged("zh_TW");
                language.setText(R.string.chinese_traditional_hk);
                return true;
            case R.id.lanEnglish:
                onLanguageChanged("en");
                language.setText(R.string.english);
                return true;
            case R.id.lanFrance:
                onLanguageChanged("fr");
                language.setText(R.string.french);
                return true;
            case R.id.lanArabic:
                onLanguageChanged("ar");
                language.setText(R.string.arabic);
                return true;
            default:
                return false;
        }
    }

    private void onLanguageChanged(String code) {
        boolean languageChanged = LocaleUtils.setDefaultLanguage(code);
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
                if (UIUtils.isDeviceReady(this)) {
                    loginPresenter.checkEmail(emailEditText.getText().toString().trim());
                }
                break;
        }
    }

    @Override
    public void onExternalLoginSuccess(ExternalAuthorize extAuthorize) {
        hideLoading();
        authorize = extAuthorize;
        if (checkLocationResponse != null) {
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
            loginPresenter.externalAuth(extAuthorize);
        }
    }

    @Override
    public void onExternalLoginStart() {
        showLoading(false);
    }

    @Override
    public void onExternalLoginFinished() {
        hideLoading();
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        switch (networkError.getErrorCode()) {
            case NetworkError.NO_INTERNET:
                DialogUtils.showBadOrNoInternetDialog(this);
                break;
            case NetworkError.ACCOUNT_NOT_ACTIVATED_ERROR_CODE:
            case NetworkError.EMAIL_SENT_ERROR:
                DialogUtils.showAccountNotActivatedDialog(this);
                break;
            default:
                UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
                break;
        }
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

    @Override
    public void onExternalAuth(ExternalAuthResponse response) {
        if (response.isRegistrationRequested()) {
            startRegistrationFlow(RegistrationType.SOCIAL, -1);
        } else if (response.isShowTermsConditions()) {
            Intent intent = new Intent(this, TermsAndConditionActivity.class);
            intent.putExtra(Keys.SHOULD_SHOW_MAIN_SCREEN, true);
            startActivity(intent);
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            preferencesManager.setTandCShowedForCurrentUser();
            finish();
        }
    }

    @Override
    public void onEmailExist(String email) {
        Intent i = new Intent(this, PasswordActivity.class);
        i.putExtra(LoginActivity.START_PUSH_NOTIFICATIONS_ACTIVITY, startPushNotificationActivity);
        i.putExtra(PasswordActivity.EMAIL, email);
        fillIntentWithLocationData(i);
        startActivity(i);
    }

    @Override
    public void onEmailFieldEmpty() {
        UIUtils.showSimpleToast(this, R.string.fill_in_field);
    }

    @Override
    public void onNotAllFilesSent() {
        DialogUtils.showNotAllFilesSendDialog(this);
    }

    @Override
    public void startRegistrationFlow(RegistrationType type, int registrationBitMask) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localReceiver);
        checkLocationPresenter.detachView();
        loginPresenter.detachView();
        if (checkLocationDialog != null && checkLocationDialog.isShowing()) {
            checkLocationDialog.dismiss();
        }
    }
}
