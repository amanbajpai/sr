package com.matrix.activity;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.matrix.*;
import com.matrix.helpers.APIFacade;
import com.matrix.location.MatrixLocationManager;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.net.gcm.CommonUtilities;
import com.matrix.utils.DialogUtils;
import com.matrix.utils.L;
import com.matrix.utils.PreferencesManager;
import com.matrix.utils.UIUtils;

import java.io.IOException;

import static com.google.android.gms.common.GooglePlayServicesUtil.*;

/**
 * Activity for Agents login into system
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private APIFacade apiFacade = APIFacade.getInstance();
    private GoogleCloudMessaging gcm;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

        final MatrixLocationManager lm = new MatrixLocationManager(getApplicationContext());
        Location loc = lm.getLocation();
        L.i(TAG, "[LOC = " + loc + "]");
        lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
            @Override
            public void onUpdate(Location location) {
                L.i(TAG, "[NEW LOC = " + location + "]");
            }
        });

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            String regId = PreferencesManager.getInstance().getGCMRegistrationId();
            if ("".equals(regId)) {
                registerGCMInBackground();
            }
        } else {
            L.i(TAG, "No valid Google Play Services APK found.");
        }

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        findViewById(R.id.registerButton).setOnClickListener(this);

        setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.LOGIN_OPERATION_TAG.equals(operation.getTag())) {
                //LoginResponse loginResponse = (LoginResponse) operation.getResponseEntities().get(0);
                UIUtils.showSimpleToast(LoginActivity.this, R.string.success);
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
        } else {
            loginButton.setEnabled(true);
            UIUtils.showSimpleToast(this, R.string.credentials_wrong);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (!UIUtils.isOnline(this)) {
                    DialogUtils.showNetworkDialog(this);
                } else if (!UIUtils.isGpsEnabled(this)) {
                    DialogUtils.showLocationDialog(this);
                } else if (!UIUtils.isGooglePlayServicesEnabled(this)) {
                    DialogUtils.showGoogleSdkDialog(this);
                } else if (UIUtils.isMockLocationEnabled(this)) {
                    DialogUtils.showMockLocationDialog(this);
                } else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    UIUtils.showSimpleToast(this, R.string.credentials_wrong);
                } else {
                    loginButton.setEnabled(false);
                    setSupportProgressBarIndeterminateVisibility(true);
                    apiFacade.login(this, email, password);
                }

                break;
            case R.id.registerButton:
                startActivity(new Intent(this, CheckLocationActivity.class));
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

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerGCMInBackground() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(App.getInstance());
                    }
                    String regId = gcm.register(CommonUtilities.SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;
                    L.i(TAG, msg);

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    APIFacade.getInstance().registerGCMId(regId);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    PreferencesManager.getInstance().setGCMRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    L.e(TAG, msg);
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
        int resultCode = isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (isUserRecoverableError(resultCode)) {
                getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                L.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
