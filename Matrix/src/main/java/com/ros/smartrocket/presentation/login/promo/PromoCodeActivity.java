package com.ros.smartrocket.presentation.login.promo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.activity.MainActivity;
import com.ros.smartrocket.presentation.login.registration.RegistrationActivity;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.login.external.ExternalAuthDetailsActivity;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PromoCodeActivity extends BaseActivity implements PromoMvpView {

    @BindView(R.id.promoCode)
    EditText promoCodeEdt;

    private PromoMvpPresenter<PromoMvpView> presenter;
    private RegistrationType type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo_code);
        ButterKnife.bind(this);
        iniUI();
        handleArgs();
    }

    private void handleArgs() {
        type = (RegistrationType) getIntent().getSerializableExtra(Keys.REGISTRATION_TYPE);
    }

    private void iniUI() {
        hideActionBar();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        checkDeviceSettingsByOnResume(false);
        presenter = new PromoPresenter<>();
        presenter.attachView(this);
    }

    public void continueRegistrationFlow() {
        Intent intent;
        if (type == RegistrationType.SOCIAL) {
            intent = new Intent(this, MainActivity.class);
        } else if (type == RegistrationType.SOCIAL_ADDITIONAL_INFO) {
            intent = new Intent(this, ExternalAuthDetailsActivity.class);
        } else {
            intent = new Intent(this, RegistrationActivity.class);
        }
        intent.putExtras(getIntent().getExtras());
        intent.putExtra(Keys.PROMO_CODE, promoCodeEdt.getText().toString());
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.continueButton)
    public void onClick() {
        presenter.sendPromoCode(promoCodeEdt.getText().toString());
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        if (networkError.getErrorCode() == NetworkError.NO_INTERNET)
            DialogUtils.showBadOrNoInternetDialog(this);
        else
            UIUtils.showSimpleToast(this, networkError.getErrorMessageRes(), Toast.LENGTH_LONG, Gravity.BOTTOM);
    }

    @Override
    public void paramsNotValid() {
        continueRegistrationFlow();
    }

    @Override
    public void onPromoCodeSent() {
        continueRegistrationFlow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
