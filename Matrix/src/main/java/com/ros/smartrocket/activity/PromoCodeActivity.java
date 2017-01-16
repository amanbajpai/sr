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
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/* This activity asks for a promo code to a user and redirect him to ReferralCodeActivity.
    we resend getIntent() information forward to use it later
*/
public class PromoCodeActivity extends BaseActivity implements NetworkOperationListenerInterface {

    @Bind(R.id.promoCode)
    EditText promoCodeEdt;
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;

    public PromoCodeActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_promo_code);
        ButterKnife.bind(this);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        checkDeviceSettingsByOnResume(false);
    }

    public void continueRegistrationFlow() {
        Intent intent;
        if (getIntent().getExtras().getBoolean(Keys.IS_SOCIAL)) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, RegistrationActivity.class);
            intent.putExtras(getIntent().getExtras());
            intent.putExtra(Keys.PROMO_CODE, promoCodeEdt.getText().toString());
        }
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

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
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

    @OnClick(R.id.continueButton)
    public void onClick() {
        String promoCode = promoCodeEdt.getText().toString();
        if (getIntent().getExtras().getBoolean(Keys.IS_SOCIAL) && !TextUtils.isEmpty(promoCode)) {
            progressDialog = CustomProgressDialog.show(this);
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
