package com.ros.smartrocket.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.android.gms.common.ConnectionResult;
import com.ros.smartrocket.R;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.MainActivity;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.net.gcm.CommonUtilities;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import static com.google.android.gms.common.GooglePlayServicesUtil.*;

/**
 * Activity for Agents login into system
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private APIFacade apiFacade = APIFacade.getInstance();
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

                // Check if we are registered on Server side our GCM Id
                if(!PreferencesManager.getInstance().isGCMIdRegisteredOnServer()) {
                    String regId = PreferencesManager.getInstance().getGCMRegistrationId();
                    if ("".equals(regId)) {
                        CommonUtilities.registerGCMInBackground();
                    } else {
                        APIFacade.getInstance().registerGCMId(App.getInstance(), regId);
                    }
                }
                // Start MainActivity
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
        } else {
            loginButton.setEnabled(true);
            DialogUtils.showRegistrationFailedDialog(this);
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
                    DialogUtils.showRegistrationFailedDialog(this);
                } else {
                    loginButton.setEnabled(false);
                    setSupportProgressBarIndeterminateVisibility(true);
                    apiFacade.login(this, email, password);
                }

                break;
            case R.id.registerButton:
                startActivity(new Intent(this, EnterGroupCodeActivity.class));
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
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean isPlayServicesAvailable(Context context) {
        int resultCode = isGooglePlayServicesAvailable(context);
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