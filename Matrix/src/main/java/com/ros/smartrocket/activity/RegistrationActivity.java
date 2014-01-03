package com.ros.smartrocket.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.ros.smartrocket.App;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.dialog.RegistrationSuccessDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.SelectImageManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;

/**
 * Activity for first Agents registration into system
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private static final int[] EDUCATION_LEVEL_CODE = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    private String[] EDUCATION_LEVEL = new String[]{};
    private static final int[] EMPLOYMENT_STATUS_CODE = new int[]{0, 1, 2, 3, 4, 5, 6};
    private String[] EMPLOYMENT_STATUS = new String[]{};

    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private SelectImageManager selectImageManager = SelectImageManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ImageView profilePhotoImageView;
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
    private Spinner educationLevelSpinner;
    private Spinner employmentStatusSpinner;
    private Bitmap photoBitmap;

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

        EDUCATION_LEVEL = new String[]{getString(R.string.education_level), getString(R.string.no_schooling),
                getString(R.string.primary_school), getString(R.string.junior_secondary_school),
                getString(R.string.senior_secondary_school), getString(R.string.vocational_collage),
                getString(R.string.bachelors_degree), getString(R.string.master_degree_or_higher)};
        EMPLOYMENT_STATUS = new String[]{getString(R.string.employment_status), getString(R.string.student),
                getString(R.string.employed_part_time), getString(R.string.employed_full_time),
                getString(R.string.not_employed_looking_for_work),
                getString(R.string.not_employed_not_looking_for_work), getString(R.string.retired)};


        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        profilePhotoImageView.setOnClickListener(this);

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

        educationLevelSpinner = (Spinner) findViewById(R.id.educationLevelSpinner);
        ArrayAdapter educationLevelAdapter = new ArrayAdapter<String>(this, R.layout.list_item_spinner, R.id.name,
                EDUCATION_LEVEL);
        educationLevelSpinner.setAdapter(educationLevelAdapter);

        employmentStatusSpinner = (Spinner) findViewById(R.id.employmentStatusSpinner);
        ArrayAdapter employmentStatusAdapter = new ArrayAdapter<String>(this, R.layout.list_item_spinner, R.id.name,
                EMPLOYMENT_STATUS);
        employmentStatusSpinner.setAdapter(employmentStatusAdapter);

        agreeCheckBox = (CheckBox) findViewById(R.id.agreeCheckBox);

        findViewById(R.id.confirmButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);

        countryEditText.setText(countryName);
        cityEditText.setText(cityName);

        checkMockLocationByOnResume(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profilePhotoImageView:
                selectImageManager.showSelectImageDialog(this, true, new SelectImageManager.OnImageCompleteListener() {
                    @Override
                    public void onImageComplete(Bitmap bitmap) {
                        RegistrationActivity.this.photoBitmap = bitmap;
                        if (bitmap != null) {
                            profilePhotoImageView.setImageBitmap(bitmap);
                        } else {
                            profilePhotoImageView.setImageResource(R.drawable.no_photo);
                        }
                    }
                });
                break;
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

                if(!UIUtils.isEmailValid(email)){
                    UIUtils.showSimpleToast(this, R.string.fill_in_field);
                    break;
                }

                int educationLevel = EDUCATION_LEVEL_CODE[educationLevelSpinner.getSelectedItemPosition()];
                int employmentStatus = EMPLOYMENT_STATUS_CODE[employmentStatusSpinner.getSelectedItemPosition()];
                if (educationLevel == 0 || employmentStatus == 0) {
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
                registrationEntity.setGroupCode(groupCode);
                registrationEntity.setEducationLevel(educationLevel);
                registrationEntity.setEmploymentStatus(employmentStatus);

                if (photoBitmap != null) {
                    registrationEntity.setPhotoBase64(BytesBitmap.getBase64String(photoBitmap));
                }

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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        selectImageManager.onActivityResult(requestCode, resultCode, intent);
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
