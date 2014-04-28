package com.ros.smartrocket.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

import java.util.List;

public class ActivateAccountActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private static final String TAG = ActivateAccountActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;
    private Button activateAccountButton;

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
            Uri data = getIntent().getData();
            if (data != null) {
                String scheme = data.getScheme(); // "http"
                String host = data.getHost(); // "twitter.com"
                List<String> params = data.getPathSegments();
                String first = params.get(0); // "status"
                String second = params.get(1); // "1234"

                L.e(TAG, "scheme: " + scheme + "host: " + host + "first: " + first + "second: " + second);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activateAccountButton:
                if (!UIUtils.isOnline(this)) {
                    DialogUtils.showNetworkDialog(this);
                } else {
                    progressDialog = CustomProgressDialog.show(this);
                    activateAccountButton.setEnabled(false);

                    apiFacade.activateAccount(this, "ActivationCode");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.ACTIVATE_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.dismiss();
                DialogUtils.showAccountConfirmedDialog(this);
            }

        } else {
            if (Keys.ACTIVATE_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.dismiss();
                activateAccountButton.setEnabled(true);
                UIUtils.showSimpleToast(this, operation.getResponseError());
            }
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
