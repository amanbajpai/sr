package com.matrix.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.db.entity.SubscriptionResponse;
import com.matrix.helpers.APIFacade;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.UIUtils;

public class CheckLocationFailedActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    public final static String TAG = CheckLocationFailedActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    public EditText countryEditText;
    public EditText cityEditText;
    public EditText emailEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_failed);

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        countryEditText = (EditText) findViewById(R.id.countryEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);

        findViewById(R.id.subscribeButton).setOnClickListener(this);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.SUBSCRIBE_OPERATION_TAG.equals(operation.getTag())) {
                SubscriptionResponse subscriptionResponse = (SubscriptionResponse) operation.getResponseEntities().get(0);
                if (subscriptionResponse.getState()) {
                    UIUtils.showSimpleToast(this, R.string.success);
                }
            }
        } else {
            UIUtils.showSimpleToast(this, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribeButton:
                String countryName = countryEditText.getText().toString().trim();
                String cityName = cityEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();

                apiFacade.subscribe(this, email, countryName, cityName);
                break;
            case R.id.cancelButton:

                break;
            default:
                break;
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
}
