package com.ros.smartrocket.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.dialog.DatePickerDialog;
import com.ros.smartrocket.dialog.RegistrationSuccessDialog;
import com.ros.smartrocket.eventbus.AvatarEvent;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationFieldTextWatcher;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.image.AvatarImageManager;
import com.ros.smartrocket.utils.image.SelectImageManager;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomEditTextView;
import com.ros.smartrocket.views.CustomTextView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


/**
 * Activity for first Agents registration into system
 */
public class RegistrationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, CompoundButton.OnCheckedChangeListener {
    private static final String STATE_PHOTO = "com.ros.smartrocket.RegistrationActivity.STATE_PHOTO";

    @Bind(R.id.profilePhotoImageView)
    ImageView profilePhotoImageView;
    @Bind(R.id.firstNameEditText)
    CustomEditTextView fullNameEditText;
    @Bind(R.id.maleRadioButton)
    RadioButton maleRadioButton;
    @Bind(R.id.femaleRadioButton)
    RadioButton femaleRadioButton;
    @Bind(R.id.genderRadioGroup)
    RadioGroup genderRadioGroup;
    @Bind(R.id.birthdayEditText)
    CustomEditTextView birthdayEditText;
    @Bind(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @Bind(R.id.emailValidationText)
    CustomTextView emailValidationText;
    @Bind(R.id.passwordEditText)
    CustomEditTextView passwordEditText;
    @Bind(R.id.passwordValidationText)
    CustomTextView passwordValidationText;
    @Bind(R.id.showPasswordToggleButton)
    ToggleButton showPasswordToggleButton;
    @Bind(R.id.confirmButton)
    CustomButton confirmButton;
    @Bind(R.id.cancelButton)
    CustomButton cancelButton;

    private APIFacade apiFacade = APIFacade.getInstance();
    private Long selectedBirthDay = null;
    private int referralCasesId;
    private int districtId;
    private int countryId;
    private int cityId;
    private String groupCode = "";
    private Double latitude;
    private Double longitude;
    private Bitmap photoBitmap;
    private CustomProgressDialog progressDialog;
    private String promoCode;
    private File mCurrentPhotoFile;
    private AvatarImageManager avatarImageManager;
    private boolean isTrmsShowed;

    public RegistrationActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        avatarImageManager = new AvatarImageManager();
        if (getIntent() != null) {
            districtId = getIntent().getIntExtra(Keys.DISTRICT_ID, 0);
            countryId = getIntent().getIntExtra(Keys.COUNTRY_ID, 0);
            cityId = getIntent().getIntExtra(Keys.CITY_ID, 0);
            groupCode = getIntent().getStringExtra(Keys.GROUP_CODE);
            latitude = getIntent().getDoubleExtra(Keys.LATITUDE, 0);
            longitude = getIntent().getDoubleExtra(Keys.LONGITUDE, 0);
            referralCasesId = getIntent().getIntExtra(Keys.REFERRAL_CASES_ID, 0);
            promoCode = getIntent().getStringExtra(Keys.PROMO_CODE);
            isTrmsShowed = getIntent().getBooleanExtra(Keys.T_AND_C, false);
        }

        profilePhotoImageView.setOnClickListener(this);
        maleRadioButton.setOnClickListener(this);
        femaleRadioButton.setOnClickListener(this);
        birthdayEditText.setOnClickListener(this);
        showPasswordToggleButton.setOnCheckedChangeListener(this);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        checkDeviceSettingsByOnResume(false);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PHOTO)) {
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
        }
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
                mCurrentPhotoFile = SelectImageManager.getTempFile(this, SelectImageManager.PREFIX_PROFILE);
                avatarImageManager.showSelectImageDialog(this, true, mCurrentPhotoFile);
                break;
            case R.id.confirmButton:
                String fullName = fullNameEditText.getText().toString().trim();

                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                UIUtils.setEditTextColorByState(this, fullNameEditText, !TextUtils.isEmpty(fullName));
                UIUtils.setEditTextColorByState(this, birthdayEditText, selectedBirthDay != null);

                UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
                UIUtils.setEmailEditTextImageByState(emailEditText, UIUtils.isEmailValid(email));

                UIUtils.setRadioButtonsByState(maleRadioButton, femaleRadioButton,
                        genderRadioGroup.getCheckedRadioButtonId() != -1);

                boolean isPasswordValid = UIUtils.isPasswordValid(password);
                UIUtils.setEditTextColorByState(this, passwordEditText, isPasswordValid);
                UIUtils.setPasswordEditTextImageByState(passwordEditText, isPasswordValid);
                passwordValidationText.setVisibility(isPasswordValid ? View.GONE : View.VISIBLE);

                fullNameEditText.clearFocus();
                emailEditText.clearFocus();
                passwordEditText.clearFocus();

                if (TextUtils.isEmpty(fullName) || !UIUtils.isEmailValid(email)
                        || selectedBirthDay == null || !UIUtils.isPasswordValid(password)
                        || genderRadioGroup.getCheckedRadioButtonId() == -1) {

                    UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);

                    fullNameEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, fullNameEditText));
                    birthdayEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, birthdayEditText));
                    emailEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, emailEditText));
                    passwordEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, passwordEditText,
                            passwordValidationText));

                    if (TextUtils.isEmpty(fullName)) {
                        fullNameEditText.requestFocus();
                    } else if (selectedBirthDay == null) {
                        fullNameEditText.requestFocus();
                        fullNameEditText.clearFocus();
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
                registrationEntity.setFullName(fullName);
                registrationEntity.setBirthday(UIUtils.longToString(selectedBirthDay, 2));
                registrationEntity.setDistrictId(districtId);
                registrationEntity.setCountryId(countryId);
                registrationEntity.setCityId(cityId);
                registrationEntity.setLatitude(latitude);
                registrationEntity.setLongitude(longitude);
                registrationEntity.setGroupCode(groupCode);
                registrationEntity.setTermsShowed(true);

                if (referralCasesId > 0) {
                    registrationEntity.setReferralId(referralCasesId);
                }

                if (photoBitmap != null) {
                    registrationEntity.setPhotoBase64(BytesBitmap.getBase64String(photoBitmap));
                }

                if (promoCode != null) {
                    registrationEntity.setPromoCode(promoCode);
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
            case R.id.cancelButton:
                finish();
                break;
            case R.id.maleRadioButton:
                // go through
            case R.id.femaleRadioButton:
                UIUtils.setRadioButtonsByState(maleRadioButton, femaleRadioButton, true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.REGISTRATION_OPERATION_TAG.equals(operation.getTag())) {
                PreferencesManager.getInstance().setTandCShowed(emailEditText.getText().toString().trim());
                new RegistrationSuccessDialog(this, emailEditText.getText().toString().trim());
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(STATE_PHOTO, mCurrentPhotoFile);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null && intent.getData() != null) {
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, SelectImageManager.PREFIX_PROFILE);
            avatarImageManager.onActivityResult(requestCode, resultCode, intent, this);
        } else if (mCurrentPhotoFile != null) {
            intent = new Intent();
            intent.putExtra(SelectImageManager.EXTRA_PHOTO_FILE, mCurrentPhotoFile);
            intent.putExtra(SelectImageManager.EXTRA_PREFIX, SelectImageManager.PREFIX_PROFILE);
            avatarImageManager.onActivityResult(requestCode, resultCode, intent, this);
        }
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        removeNetworkOperationListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(AvatarEvent event) {
        switch (event.type) {
            case START_LOADING:
                setSupportProgressBarIndeterminateVisibility(true);
                break;
            case IMAGE_COMPLETE:
                if (event.image != null && event.image.bitmap != null) {
                    RegistrationActivity.this.photoBitmap = event.image.bitmap;
                    profilePhotoImageView.setImageBitmap(event.image.bitmap);
                } else {
                    profilePhotoImageView.setImageResource(R.drawable.btn_camera_error_selector);
                }
                setSupportProgressBarIndeterminateVisibility(false);
                break;
            case SELECT_IMAGE_ERROR:
                setSupportProgressBarIndeterminateVisibility(false);
                DialogUtils.showPhotoCanNotBeAddDialog(RegistrationActivity.this);
                break;
        }
    }
}