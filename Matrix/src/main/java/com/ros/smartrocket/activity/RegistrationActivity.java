package com.ros.smartrocket.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.db.entity.TermsAndConditionVersion;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.dialog.DatePickerDialog;
import com.ros.smartrocket.dialog.RegistrationSuccessDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.RegistrationFieldTextWatcher;
import com.ros.smartrocket.utils.SelectImageManager;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Activity for first Agents registration into system
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    private static final int[] EDUCATION_LEVEL_CODE = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    private static final int[] EMPLOYMENT_STATUS_CODE = new int[]{0, 1, 2, 3, 4, 5, 6};

    private SelectImageManager selectImageManager = SelectImageManager.getInstance();
    private APIFacade apiFacade = APIFacade.getInstance();
    private ImageView profilePhotoImageView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText passwordEditText;
    private EditText birthdayEditText;
    private EditText emailEditText;
    private TextView emailValidationText;
    private TextView passwordValidationText;
    private CheckBox agreeCheckBox;
    private Long selectedBirthDay = null;
    private int countryId;
    private int cityId;
    private String countryName;
    private String cityName;
    private String groupCode = "";
    private RadioGroup genderRadioGroup;
    private Double latitude;
    private Double longitude;
    private Spinner educationLevelSpinner;
    private Spinner employmentStatusSpinner;
    private Bitmap photoBitmap;
    private CustomProgressDialog progressDialog;
    private int currentTermsAndConditionsVersion = 1;

    public RegistrationActivity() {
    }

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

        String[] educationLevel = new String[]{getString(R.string.education_level), getString(R.string.no_schooling),
                getString(R.string.primary_school), getString(R.string.junior_secondary_school),
                getString(R.string.senior_secondary_school), getString(R.string.vocational_collage),
                getString(R.string.bachelors_degree), getString(R.string.master_degree_or_higher)};
        String[] employmentStatus = new String[]{getString(R.string.employment_status), getString(R.string.student),
                getString(R.string.employed_part_time), getString(R.string.employed_full_time),
                getString(R.string.not_employed_looking_for_work),
                getString(R.string.not_employed_not_looking_for_work), getString(R.string.retired)};

        profilePhotoImageView = (ImageView) findViewById(R.id.profilePhotoImageView);
        profilePhotoImageView.setOnClickListener(this);

        firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        genderRadioGroup = (RadioGroup) findViewById(R.id.genderRadioGroup);

        emailValidationText = (TextView) findViewById(R.id.emailValidationText);
        passwordValidationText = (TextView) findViewById(R.id.passwordValidationText);

        birthdayEditText = (EditText) findViewById(R.id.birthdayEditText);
        birthdayEditText.setOnClickListener(this);

        ((ToggleButton) findViewById(R.id.showPasswordToggleButton)).setOnCheckedChangeListener(this);

        educationLevelSpinner = (Spinner) findViewById(R.id.educationLevelSpinner);
        ArrayAdapter educationLevelAdapter = new ArrayAdapter<String>(this, R.layout.list_item_spinner, R.id.name,
                educationLevel);
        educationLevelSpinner.setAdapter(educationLevelAdapter);

        employmentStatusSpinner = (Spinner) findViewById(R.id.employmentStatusSpinner);
        ArrayAdapter employmentStatusAdapter = new ArrayAdapter<String>(this, R.layout.list_item_spinner, R.id.name,
                employmentStatus);
        employmentStatusSpinner.setAdapter(employmentStatusAdapter);

        agreeCheckBox = (CheckBox) findViewById(R.id.agreeCheckBox);


        findViewById(R.id.termsAndConditionsButton).setOnClickListener(this);
        findViewById(R.id.confirmButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);

        ((EditText) findViewById(R.id.countryEditText)).setText(countryName);
        ((EditText) findViewById(R.id.cityEditText)).setText(cityName);

        checkDeviceSettingsByOnResume(false);

        apiFacade.getCurrentTermsAndConditionVersion(this);
    }

    public void setAgreeCheckBoxText() {
        SpannableString ss = new SpannableString(getString(R.string.i_agree_with_matrix_term));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(IntentUtils.getTermsAndConditionIntent(RegistrationActivity.this,
                        currentTermsAndConditionsVersion));
            }
        };
        ss.setSpan(clickableSpan, 22, 27, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        agreeCheckBox.setText(ss);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.birthdayEditText:
                new DatePickerDialog(this, selectedBirthDay, new DatePickerDialog.DialogButtonClickListener() {
                    @Override
                    public void onOkButtonPressed(long selectedDate, String selectedDateForPreview) {
                        birthdayEditText.setText(selectedDateForPreview);
                        selectedBirthDay = selectedDate;
                    }

                    @Override
                    public void onCancelButtonPressed() {

                    }
                });
                break;
            case R.id.profilePhotoImageView:
                selectImageManager.showSelectImageDialog(this, true);
                selectImageManager.setImageCompleteListener(new SelectImageManager.OnImageCompleteListener() {
                    @Override
                    public void onImageComplete(Bitmap bitmap) {
                        RegistrationActivity.this.photoBitmap = bitmap;
                        if (bitmap != null) {
                            profilePhotoImageView.setImageBitmap(bitmap);
                        } else {
                            profilePhotoImageView.setImageResource(R.drawable.btn_camera_error_selector);
                        }
                    }

                    @Override
                    public void onSelectImageError(int imageFrom) {
                        DialogUtils.showPhotoCanNotBeAddDialog(RegistrationActivity.this);
                    }
                });
                break;
            case R.id.confirmButton:
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                int educationLevel = EDUCATION_LEVEL_CODE[educationLevelSpinner.getSelectedItemPosition()];
                int employmentStatus = EMPLOYMENT_STATUS_CODE[employmentStatusSpinner.getSelectedItemPosition()];

                UIUtils.setEditTextColorByState(this, firstNameEditText, !TextUtils.isEmpty(firstName));
                UIUtils.setEditTextColorByState(this, lastNameEditText, !TextUtils.isEmpty(lastName));
                UIUtils.setEditTextColorByState(this, birthdayEditText, selectedBirthDay != null);

                UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
                UIUtils.setEmailEditTextImageByState(emailEditText, UIUtils.isEmailValid(email));

                boolean isPasswordValid = UIUtils.isPasswordValid(password);
                UIUtils.setEditTextColorByState(this, passwordEditText, isPasswordValid);
                UIUtils.setPasswordEditTextImageByState(passwordEditText, isPasswordValid);
                passwordValidationText.setVisibility(isPasswordValid ? View.GONE : View.VISIBLE);

                UIUtils.setSpinnerBackgroundByState(educationLevelSpinner, educationLevel != 0);
                UIUtils.setSpinnerBackgroundByState(employmentStatusSpinner, employmentStatus != 0);

                UIUtils.setCheckBoxBackgroundByState(agreeCheckBox, agreeCheckBox.isChecked());

                firstNameEditText.clearFocus();
                lastNameEditText.clearFocus();
                emailEditText.clearFocus();
                passwordEditText.clearFocus();

                if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || !UIUtils.isEmailValid(email)
                        || selectedBirthDay == null || !UIUtils.isPasswordValid(password) || educationLevel == 0 ||
                        employmentStatus == 0 || !agreeCheckBox.isChecked()/* || photoBitmap == null*/) {
                    UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);


                    firstNameEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, firstNameEditText));
                    lastNameEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, lastNameEditText));
                    birthdayEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, birthdayEditText));
                    emailEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, emailEditText));
                    passwordEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, passwordEditText,
                            passwordValidationText));

                    educationLevelSpinner.setOnItemSelectedListener(this);
                    employmentStatusSpinner.setOnItemSelectedListener(this);

                    if (TextUtils.isEmpty(firstName)) {
                        firstNameEditText.requestFocus();
                    } else if (TextUtils.isEmpty(lastName)) {
                        lastNameEditText.requestFocus();
                    } else if (selectedBirthDay == null) {
                        lastNameEditText.requestFocus();
                        lastNameEditText.clearFocus();
                    } else if (!UIUtils.isEmailValid(email)) {
                        emailEditText.requestFocus();
                    } else if (!UIUtils.isPasswordValid(password)) {
                        passwordEditText.requestFocus();
                    }

                    break;
                }

                Registration registrationEntity = new Registration();
                registrationEntity.setEmail(email);
                registrationEntity.setPassword(password);
                registrationEntity.setFirstName(firstName);
                registrationEntity.setLastName(lastName);
                registrationEntity.setBirthday(UIUtils.longToString(selectedBirthDay, 2));
                registrationEntity.setCountryId(countryId);
                registrationEntity.setCityId(cityId);
                registrationEntity.setLatitude(latitude);
                registrationEntity.setLongitude(longitude);
                registrationEntity.setGroupCode(groupCode);
                registrationEntity.setEducationLevel(educationLevel);
                registrationEntity.setEmploymentStatus(employmentStatus);
                registrationEntity.setTermsAndConditionsVersion(currentTermsAndConditionsVersion);

                if (photoBitmap != null) {
                    registrationEntity.setPhotoBase64(BytesBitmap.getBase64String(photoBitmap));
                }

                switch (genderRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.maleRadioButton:
                        registrationEntity.setGender(1);
                        break;
                    case R.id.femaleRadioButton:
                        registrationEntity.setGender(2);
                        break;
                    default:
                        break;
                }

                progressDialog = CustomProgressDialog.show(this);
                progressDialog.setCancelable(false);
                apiFacade.registration(this, registrationEntity);
                break;
            case R.id.termsAndConditionsButton:
                startActivity(IntentUtils.getTermsAndConditionIntent(this, currentTermsAndConditionsVersion));
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
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.REGISTRATION_OPERATION_TAG.equals(operation.getTag())) {
                new RegistrationSuccessDialog(this, emailEditText.getText().toString().trim());
            } else if (Keys.GET_CURRENT_T_AND_C_OPERATION_TAG.equals(operation.getTag())) {
                TermsAndConditionVersion version = (TermsAndConditionVersion) operation.getResponseEntities().get(0);
                currentTermsAndConditionsVersion = version.getVersion();
            }

        } else if (operation.getResponseErrorCode() != null && operation.getResponseErrorCode() == BaseNetworkService
                .USER_ALREADY_EXISTS_ERROR_CODE) {
            if (Keys.REGISTRATION_OPERATION_TAG.equals(operation.getTag())) {
                DialogUtils.showUserAlreadyExistDialog(this);
                UIUtils.setEditTextColorByState(this, emailEditText, false);
                UIUtils.setEmailEditTextImageByState(emailEditText, false);
                emailValidationText.setVisibility(View.VISIBLE);
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError(), Toast.LENGTH_LONG, Gravity.BOTTOM);
            UIUtils.setEditTextColorByState(this, emailEditText, true);
            UIUtils.setEmailEditTextImageByState(emailEditText, true);
            emailValidationText.setVisibility(View.GONE);
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
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
                passwordEditText.setTypeface(FontUtils.loadFontFromAsset(getAssets(), FontUtils.getFontAssetPath(2)));
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.educationLevelSpinner:
                int educationLevel = EDUCATION_LEVEL_CODE[educationLevelSpinner.getSelectedItemPosition()];

                UIUtils.setSpinnerBackgroundByState(educationLevelSpinner, educationLevel != 0);
                break;
            case R.id.employmentStatusSpinner:
                int employmentStatus = EMPLOYMENT_STATUS_CODE[employmentStatusSpinner.getSelectedItemPosition()];

                UIUtils.setSpinnerBackgroundByState(employmentStatusSpinner, employmentStatus != 0);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        selectImageManager.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.registration_form);

        return true;
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
