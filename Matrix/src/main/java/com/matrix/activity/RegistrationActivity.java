package com.matrix.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.db.entity.Registration;
import com.matrix.db.entity.RegistrationResponse;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.net.WSUrl;
import com.matrix.utils.UIUtils;

public class RegistrationActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = RegistrationActivity.class.getSimpleName();
    private static final String REGISTRETION_OPERATION_TAG = "login_operation_tag";
    public EditText fullNameEditText;
    public EditText passwordEditText;
    public EditText dayEditText;
    public EditText monthEditText;
    public EditText yearEditText;
    public EditText emailEditText;
    public EditText countryEditText;
    public EditText cityEditText;
    public CheckBox agreeCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        fullNameEditText = (EditText) findViewById(R.id.fullNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        dayEditText = (EditText) findViewById(R.id.dayEditText);
        monthEditText = (EditText) findViewById(R.id.monthEditText);
        yearEditText = (EditText) findViewById(R.id.yearEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        countryEditText = (EditText) findViewById(R.id.countryEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);

        agreeCheckBox = (CheckBox) findViewById(R.id.agreeCheckBox);

        findViewById(R.id.confirmButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);


    }

    private void registration() {
        String email = emailEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(fullName)) {

            Registration registrationEntity = new Registration();
            registrationEntity.setMail(email);
            registrationEntity.setFullName(fullName);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.REGISTRATION);
            operation.setTag(REGISTRETION_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(registrationEntity);
            sendNetworkOperation(operation);
        } else {
            UIUtils.showSimpleToast(this, R.string.fill_in_field);
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (REGISTRETION_OPERATION_TAG.equals(operation.getTag())) {
                RegistrationResponse registrationResponse = (RegistrationResponse) operation.getResponseEntities().get(0);
                if (registrationResponse.getState()) {
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
            case R.id.confirmButton:
                registration();
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
