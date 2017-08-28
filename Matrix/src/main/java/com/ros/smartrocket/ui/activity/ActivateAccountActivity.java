package com.ros.smartrocket.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.ui.dialog.CustomProgressDialog;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.UIUtils;

public class ActivateAccountActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;
    private Button activateAccountButton;
    private String email;
    private String token;

    public ActivateAccountActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_activate_account);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        activateAccountButton = (Button) findViewById(R.id.activateAccountButton);
        activateAccountButton.setOnClickListener(this);

        checkDeviceSettingsByOnResume(false);

        if (getIntent() != null) {
            email = getIntent().getStringExtra(Keys.EMAIL);
            token = getIntent().getStringExtra(Keys.TOKEN);

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
                activateAccountButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activateAccountButton:
                LocaleUtils.setCurrentLanguage();
                if (!UIUtils.isOnline(this)) {
                    DialogUtils.showNetworkDialog(this);
                } else {
                    progressDialog = CustomProgressDialog.show(this);
                    activateAccountButton.setEnabled(false);

                    apiFacade.activateAccount(this, email, token);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.ACTIVATE_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
            progressDialog.dismiss();
            DialogUtils.showAccountConfirmedDialog(this);
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.ACTIVATE_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
            progressDialog.dismiss();
            activateAccountButton.setEnabled(true);
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
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
}
