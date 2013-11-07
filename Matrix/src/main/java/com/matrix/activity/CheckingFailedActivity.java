package com.matrix.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.db.entity.Registration;
import com.matrix.db.entity.RegistrationResponse;
import com.matrix.db.entity.Subscription;
import com.matrix.db.entity.SubscriptionResponse;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.net.WSUrl;
import com.matrix.utils.UIUtils;

public class CheckingFailedActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = CheckingFailedActivity.class.getSimpleName();
    private static final String SUBSCRIBE_OPERATION_TAG = "subscribe_operation_tag";
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

    private void subscribe() {
        String countryName = countryEditText.getText().toString().trim();
        String cityName = cityEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(countryName) && !TextUtils.isEmpty(cityName)) {

            Subscription subscriptionEntity = new Subscription();
            subscriptionEntity.setMail(email);
            subscriptionEntity.setCountry(countryName);
            subscriptionEntity.setCountry(cityName);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.SUBSCRIPTION);
            operation.setTag(SUBSCRIBE_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(subscriptionEntity);
            sendNetworkOperation(operation);
        } else {
            UIUtils.showSimpleToast(this, R.string.fill_in_field);
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (SUBSCRIBE_OPERATION_TAG.equals(operation.getTag())) {
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
                subscribe();
                break;
            case R.id.cancelButton:

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
