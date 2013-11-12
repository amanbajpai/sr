package com.matrix.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.MainActivity;
import com.matrix.R;
import com.matrix.db.entity.LoginResponse;
import com.matrix.helpers.APIFacade;
import com.matrix.location.MatrixLocationManager;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.L;
import com.matrix.utils.UIUtils;

public class LoginActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    public EditText emailEditText;
    public EditText passwordEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final MatrixLocationManager lm = new MatrixLocationManager(getApplicationContext());
        Location loc = lm.getLocation();

        L.i(TAG, "[LOC = " + loc + "]");
        lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {

            @Override
            public void onUpdate(Location location) {
                L.i(TAG, "ASYNC [LOC = " + location + "]");

                lm.getAddress(location, new MatrixLocationManager.IAddress() {
                    @Override
                    public void onUpdate(Address address) {

                        /*
                         * Format the first line of address (if available),
                         * city, and country name.
                         */
                        /*String addressText = String.format(
                                "%s, %s, %s",
                                // If there's a street address, add it
                                address.getMaxAddressLineIndex() > 0 ?
                                        address.getAddressLine(0) : "",
                                // Locality is usually a city
                                address.getLocality(),
                                // The country of the address
                                address.getCountryName());

                        L.d(TAG, "Address =  [" + addressText + "]");*/
                    }
                });
            }
        });


        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.registerButton).setOnClickListener(this);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
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
                /*String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                apiFacade.login(this, email, password);*/
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
