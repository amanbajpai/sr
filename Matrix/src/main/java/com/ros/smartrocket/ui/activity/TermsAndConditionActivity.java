package com.ros.smartrocket.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.RegistrationPermissions;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.ui.dialog.CustomProgressDialog;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomCheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TermsAndConditionActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener,
        NetworkOperationListenerInterface {
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.acceptTC)
    CustomCheckBox acceptTC;
    @BindView(R.id.continueButton)
    CustomButton continueButton;

    private CustomProgressDialog progressDialog;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private RegistrationPermissions registrationPermissions;
    private APIFacade apiFacade = APIFacade.getInstance();
    private RegistrationType type;

    public TermsAndConditionActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_terms_and_condition);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        type = (RegistrationType) getIntent().getSerializableExtra(Keys.REGISTRATION_TYPE);
        registrationPermissions = PreferencesManager.getInstance().getRegPermissions();
        acceptTC.setOnCheckedChangeListener(this);
        webView.setWebViewClient(new WebViewClientWithProgress());
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
        webView.loadUrl(termsUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.term_and_conditions_title);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

        if (getIntent().getExtras() != null) {
            intent.putExtras(getIntent().getExtras());
        }
        intent.putExtra(Keys.T_AND_C, true);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.continueButton)
    public void onClick() {
        if (getIntent().getExtras() != null
                && (getIntent().getExtras().getBoolean(Keys.SHOULD_SHOW_MAIN_SCREEN)
                || type == RegistrationType.SOCIAL)) {
            showProgressDialog(true);
            apiFacade.sendTandC(this);
        } else {
            continueRegistrationFlow();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        continueButton.setEnabled(isChecked);
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.POST_T_AND_C_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            if (getIntent().getExtras() != null
                    && getIntent().getExtras().getBoolean(Keys.SHOULD_SHOW_MAIN_SCREEN)) {
                preferencesManager.setTandCShowedForCurrentUser();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                continueRegistrationFlow();
            }
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.POST_T_AND_C_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            if (operation.getResponseErrorCode() != null
                    && operation.getResponseErrorCode() == BaseNetworkService.NO_INTERNET) {
                DialogUtils.showBadOrNoInternetDialog(this);
            } else {
                UIUtils.showSimpleToast(this, operation.getResponseError(), Toast.LENGTH_LONG, Gravity.BOTTOM);
            }
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

    public void showProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = CustomProgressDialog.show(this);
        progressDialog.setCancelable(false);
    }

    public void dismissProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private class WebViewClientWithProgress extends WebViewClient {
        WebViewClientWithProgress() {
            showProgressBar();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dismissProgressBar();
        }
    }
}
