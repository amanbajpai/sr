package com.ros.smartrocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.RegistrationType;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PromoCodeActivity extends BaseActivity implements NetworkOperationListenerInterface {

    @BindView(R.id.promoCode)
    EditText promoCodeEdt;
    private APIFacade apiFacade = APIFacade.getInstance();
    private RegistrationType type;

    public PromoCodeActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_promo_code);
        ButterKnife.bind(this);
        type = (RegistrationType) getIntent().getSerializableExtra(Keys.REGISTRATION_TYPE);
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        checkDeviceSettingsByOnResume(false);
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

    @OnClick(R.id.continueButton)
    public void onClick() {
        String promoCode = promoCodeEdt.getText().toString();
        if (!TextUtils.isEmpty(promoCode) ) {
            showProgressDialog(false);
            apiFacade.setPromoCode(this, promoCode);
        } else {
            continueRegistrationFlow();
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.POST_PROMO_CODE_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
                continueRegistrationFlow();
            } else if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode()
                    == BaseNetworkService.NO_INTERNET) {
                DialogUtils.showBadOrNoInternetDialog(this);
            } else {
                UIUtils.showSimpleToast(this, operation.getResponseError(), Toast.LENGTH_LONG, Gravity.BOTTOM);
            }
        }
    }
}
