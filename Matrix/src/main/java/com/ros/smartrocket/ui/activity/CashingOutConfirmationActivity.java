package com.ros.smartrocket.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

public class CashingOutConfirmationActivity extends BaseActivity implements NetworkOperationListenerInterface,
        View.OnClickListener {
    private APIFacade apiFacade = APIFacade.getInstance();

    public CashingOutConfirmationActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashing_out_confirmation);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        Button continueButton = (Button) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);

    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.CASHING_OUT_OPERATION_TAG.equals(operation.getTag())) {
            hideLoading();
            finish();
            startActivity(IntentUtils.getCashOutSuccessIntent(this));
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.CASHING_OUT_OPERATION_TAG.equals(operation.getTag())) {
            UIUtils.showSimpleToast(this, operation.getResponseError());
            hideLoading();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                finish();
                break;
            case R.id.continueButton:
                //MyAccount myAccount = App.getInstance().getMyAccount();

                showLoading(false);
                apiFacade.cashingOut(this);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_title);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }
}