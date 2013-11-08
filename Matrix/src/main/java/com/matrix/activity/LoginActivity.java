package com.matrix.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.MainActivity;
import com.matrix.R;
import com.matrix.db.entity.Login;
import com.matrix.db.entity.LoginResponse;
import com.matrix.location.LocationService;
import com.matrix.location.MatrixLocationManager;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.net.WSUrl;
import com.matrix.utils.L;
import com.matrix.utils.UIUtils;

public class LoginActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private static final String LOGIN_OPERATION_TAG = "login_operation_tag";
    public EditText emailEditText;
    public EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FIXME We dont run our services for location
        //startService(new Intent(this, LocationService.class));
        MatrixLocationManager lm = new MatrixLocationManager(getApplicationContext());
        Location loc = lm.getLocation();

        L.i(TAG, "[LOC = " + loc + "]");
        lm.addRequest(new MatrixLocationManager.ILocationUpdate(){
            @Override
            public void onUpdate(Location location) {
                L.i(TAG, "ASYNC [LOC = " + location + "]");
            }
        });

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.registerButton).setOnClickListener(this);
    }

    private void login() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            Login loginEntity = new Login();
            loginEntity.setMail(email);
            loginEntity.setPassword(password);

            BaseOperation operation = new BaseOperation();
            operation.setUrl(WSUrl.LOGIN);
            operation.setTag(LOGIN_OPERATION_TAG);
            operation.setMethod(BaseOperation.Method.POST);
            operation.getEntities().add(loginEntity);
            sendNetworkOperation(operation);
        } else {
            UIUtils.showSimpleToast(this, R.string.fill_in_field);
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                LoginResponse loginResponse = (LoginResponse) operation.getResponseEntities().get(0);
                if (loginResponse.getState()) {
                    UIUtils.showSimpleToast(LoginActivity.this, R.string.success);
                }
            }
        } else {
            UIUtils.showSimpleToast(LoginActivity.this, "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                //TODO Delete
                //login();
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.registerButton:
                startActivity(new Intent(this, RegistrationActivity.class));
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
