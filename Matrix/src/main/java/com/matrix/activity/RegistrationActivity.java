package com.matrix.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.App;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.db.entity.Registration;
import com.matrix.dialog.RegistrationSuccessDialog;
import com.matrix.helpers.APIFacade;
import com.matrix.location.MatrixLocationManager;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.UIUtils;

import java.util.Calendar;

/**
 * Activity for first Agents registration into system
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, CompoundButton.OnCheckedChangeListener {
    private final static String TAG = RegistrationActivity.class.getSimpleName();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText dayEditText;
    private EditText monthEditText;
    private EditText yearEditText;
    private EditText emailEditText;
    private EditText countryEditText;
    private EditText cityEditText;
    private CheckBox agreeCheckBox;
    private int countryId;
    private int cityId;
    private String countryName;
    private String cityName;
    private String groupCode = "";
    private RadioGroup genderRadioGroup;
    private Double latitude;
    private Double longitude;
    private ToggleButton showPasswordToggleButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_registration);

        if (getIntent() != null) {
            countryId = getIntent().getIntExtra(Keys.COUNTRY_ID, 0);
            cityId = getIntent().getIntExtra(Keys.CITY_ID, 0);
            countryName = getIntent().getStringExtra(Keys.COUNTRY_NAME);
            cityName = getIntent().getStringExtra(Keys.CITY_NAME);
            groupCode = getIntent().getStringExtra(Keys.GROUP_CODE);
            latitude = getIntent().getDoubleExtra(Keys.LATITUDE, 0);
            longitude = getIntent().getDoubleExtra(Keys.LONGITUDE, 0);
        }

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        dayEditText = (EditText) findViewById(R.id.dayEditText);
        monthEditText = (EditText) findViewById(R.id.monthEditText);
        yearEditText = (EditText) findViewById(R.id.yearEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        countryEditText = (EditText) findViewById(R.id.countryEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);
        cityEditText = (EditText) findViewById(R.id.cityEditText);
        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);

        showPasswordToggleButton = (ToggleButton) findViewById(R.id.showPasswordToggleButton);
        showPasswordToggleButton.setOnCheckedChangeListener(this);

        agreeCheckBox = (CheckBox) findViewById(R.id.agreeCheckBox);

        findViewById(R.id.confirmButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);

        countryEditText.setText(countryName);
        cityEditText.setText(cityName);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirmButton:
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String day = dayEditText.getText().toString().trim();
                String month = monthEditText.getText().toString().trim();
                String year = yearEditText.getText().toString().trim();

                String birthDay;

                if (!TextUtils.isEmpty(day) && TextUtils.isDigitsOnly(day) && !TextUtils.isEmpty(month) && TextUtils
                        .isDigitsOnly(month) && !TextUtils.isEmpty(year) && TextUtils.isDigitsOnly(year)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));

                    birthDay = UIUtils.longToString(calendar.getTimeInMillis(), 2);
                } else {
                    UIUtils.showSimpleToast(this, R.string.fill_in_field);
                    break;
                }

                Registration registrationEntity = new Registration();
                registrationEntity.setEmail(email);
                registrationEntity.setPassword(password);
                registrationEntity.setFirstName(firstName);
                registrationEntity.setLastName(lastName);
                registrationEntity.setBirthday(birthDay);
                registrationEntity.setCountryId(countryId);
                registrationEntity.setCityId(cityId);
                registrationEntity.setLatitude(latitude);
                registrationEntity.setLongitude(longitude);

                switch (genderRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.maleRadioButton:
                        registrationEntity.setGender(0);
                        break;
                    case R.id.femaleRadioButton:
                        registrationEntity.setGender(1);
                        break;
                    default:
                        break;
                }

                apiFacade.registration(this, registrationEntity, agreeCheckBox.isChecked());
                break;
            case R.id.cancelButton:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.REGISTRETION_OPERATION_TAG.equals(operation.getTag())) {
                UIUtils.showSimpleToast(this, R.string.success);

                new RegistrationSuccessDialog(this, emailEditText.getText().toString().trim());
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.showPasswordToggleButton:
                String text = passwordEditText.getText().toString().trim();
                if (isChecked) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                passwordEditText.setSelection(text.length());
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
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
