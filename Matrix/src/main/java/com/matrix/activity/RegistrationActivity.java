package com.matrix.activity;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.App;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.db.entity.RegistrationResponse;
import com.matrix.helpers.APIFacade;
import com.matrix.location.MatrixLocationManager;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.L;
import com.matrix.utils.UIUtils;

import java.util.Calendar;

/**
 * Activity for first Agents registration into system
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private final static String TAG = RegistrationActivity.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    public EditText fullNameEditText;
    public EditText passwordEditText;
    public EditText dayEditText;
    public EditText monthEditText;
    public EditText yearEditText;
    public EditText emailEditText;
    public EditText countryEditText;
    public EditText cityEditText;
    public CheckBox agreeCheckBox;
    public Location currentLocation;
    public Address currentAddress;

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

    @Override
    protected void onResume() {
        super.onResume();
        Location location = lm.getLocation();

        if (location != null) {
            currentLocation = location;
        } else {
            UIUtils.showSimpleToast(this, R.string.looking_for_location);
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    L.i(TAG, "Location Updated!");
                    currentLocation = location;

                    lm.getAddress(location, new MatrixLocationManager.IAddress() {
                        @Override
                        public void onUpdate(Address address) {
                            currentAddress = address;
                            countryEditText.setText(address.getCountryName());
                            cityEditText.setText(address.getLocality());
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.REGISTRETION_OPERATION_TAG.equals(operation.getTag())) {
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
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                String fullName = fullNameEditText.getText().toString().trim();
                String day = dayEditText.getText().toString().trim();
                String month = monthEditText.getText().toString().trim();
                String year = yearEditText.getText().toString().trim();

                int countryCode = 0;
                int cityCode = 0;
                String birthDay;

                if (!TextUtils.isEmpty(day) && TextUtils.isDigitsOnly(day) && !TextUtils.isEmpty(month) && TextUtils
                        .isDigitsOnly(month) && !TextUtils.isEmpty(year) && TextUtils.isDigitsOnly(year)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
                    birthDay = String.valueOf(calendar.getTime());
                } else {
                    UIUtils.showSimpleToast(this, R.string.fill_in_field);
                    break;
                }

                if (currentLocation != null && currentAddress != null) {
                    //TODO Get country and city code
                    L.w(TAG, "Set countryId and cityId");
                } else {
                    UIUtils.showSimpleToast(this, R.string.current_location_not_defined);
                    break;
                }

                apiFacade.registration(this, email, password, fullName, birthDay, countryCode, cityCode, agreeCheckBox.isChecked());
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
