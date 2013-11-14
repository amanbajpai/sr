package com.matrix.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.matrix.*;
import com.matrix.db.entity.LoginResponse;
import com.matrix.helpers.APIFacade;
import com.matrix.location.MatrixLocationManager;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.L;
import com.matrix.utils.PreferencesManager;
import com.matrix.utils.UIUtils;

import java.io.IOException;

/**
 * Activity for Agents login into system
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    GoogleCloudMessaging gcm;
    public EditText emailEditText;
    public EditText passwordEditText;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            String regid = PreferencesManager.getInstance().getGCMRegistrationId();

            if (regid.isEmpty()) {
                registerGCMInBackground();
            }
        } else {
            L.i(TAG, "No valid Google Play Services APK found.");
        }

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

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerGCMInBackground() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(App.getInstance());
                    }
                    String regId = gcm.register(Keys.GCM_ID);
                    msg = "Device registered, registration ID=" + regId;
                    L.i(TAG, msg);

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //TODO: API call for register ID (regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    PreferencesManager.getInstance().setGCMRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }
        }.execute();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                L.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
