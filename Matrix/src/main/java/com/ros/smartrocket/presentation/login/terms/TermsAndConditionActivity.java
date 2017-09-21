package com.ros.smartrocket.presentation.login.terms;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.login.external.ExternalAuthDetailsActivity;
import com.ros.smartrocket.presentation.login.promo.PromoCodeActivity;
import com.ros.smartrocket.presentation.login.referral.ReferralCasesActivity;
import com.ros.smartrocket.presentation.login.registration.RegistrationActivity;
import com.ros.smartrocket.presentation.main.MainActivity;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomCheckBox;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsAndConditionActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,
        TermsMvpView {
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.acceptTC)
    CustomCheckBox acceptTC;
    @BindView(R.id.continueButton)
    CustomButton continueButton;

    private TermsMvpPresenter<TermsMvpView> presenter;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private RegistrationPermissions registrationPermissions;
    private RegistrationType type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);
        ButterKnife.bind(this);
        initPresenter();
        fetchArguments();
        initUI();
    }

    private void initPresenter() {
        presenter = new TermsPresenter<>();
        presenter.attachView(this);
    }

    private void initUI() {
        hideActionBar();
        acceptTC.setOnCheckedChangeListener(this);
        webView.setWebViewClient(new WebViewClientWithProgress());
        webView.loadUrl(getTermsUrl());
    }

    private void fetchArguments() {
        type = (RegistrationType) getIntent().getSerializableExtra(Keys.REGISTRATION_TYPE);
        registrationPermissions = PreferencesManager.getInstance().getRegPermissions();
    }

    public void continueRegistrationFlow() {
        Intent intent;
        if (!TextUtils.isEmpty(preferencesManager.getLastEmail())) {
            preferencesManager.setTandCShowedForCurrentUser();
        }
        if (registrationPermissions.isReferralEnable()) {
            intent = new Intent(this, ReferralCasesActivity.class);
        } else if (registrationPermissions.isSrCodeEnable()) {
            intent = new Intent(this, PromoCodeActivity.class);
        } else if (type == RegistrationType.SOCIAL) {
            intent = new Intent(this, MainActivity.class);
        } else if (type == RegistrationType.SOCIAL_ADDITIONAL_INFO) {
            intent = new Intent(this, ExternalAuthDetailsActivity.class);
        } else {
            intent = new Intent(this, RegistrationActivity.class);
        }

        if (getIntent().getExtras() != null)
            intent.putExtras(getIntent().getExtras());

        intent.putExtra(Keys.T_AND_C, true);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.continueButton)
    public void onClick() {
        if (isRequestNeeded())
            presenter.sendTermsAndConditionsViewed();
        else
            continueRegistrationFlow();
    }

    private boolean isRequestNeeded() {
        return getIntent().getExtras() != null
                && (getIntent().getExtras().getBoolean(Keys.SHOULD_SHOW_MAIN_SCREEN)
                || type == RegistrationType.SOCIAL);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        continueButton.setEnabled(isChecked);
    }

    @Override
    public void onTermsAndConditionsSent() {
        if (getIntent().getExtras() != null
                && getIntent().getExtras().getBoolean(Keys.SHOULD_SHOW_MAIN_SCREEN)) {
            preferencesManager.setTandCShowedForCurrentUser();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            continueRegistrationFlow();
        }
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        if (networkError.getErrorCode() == NetworkError.NO_INTERNET)
            DialogUtils.showBadOrNoInternetDialog(this);
        else
            UIUtils.showSimpleToast(this, networkError.getErrorMessageRes(), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @NonNull
    private String getTermsUrl() {
        String termsUrl;
        switch (preferencesManager.getLanguageCode()) {
            case "en_SG":
            case "zh_CN":
                termsUrl = "http://smart-rocket.com/zh-hans/terms/";
                break;
            case "zh":
            case "zh_TW":
            case "zh_HK":
                termsUrl = "http://smart-rocket.com/zh-hant/terms-of-service-cnt/";
                break;
            case "fr":
                termsUrl = "http://smart-rocket.com/fr/terms-of-service/";
                break;
            case "ar":
                termsUrl = "http://www.smart-rocket.com/arabic-terms-of-service/";
                break;
            default:
                termsUrl = "http://smart-rocket.com/terms-of-service/";
                break;
        }
        return termsUrl;
    }

    private class WebViewClientWithProgress extends WebViewClient {
        WebViewClientWithProgress() {
            showLoading(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideLoading();
        }
    }
}
