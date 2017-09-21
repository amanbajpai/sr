package com.ros.smartrocket.presentation.login.registration;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.interfaces.SwitchCheckedChangeListener;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.ui.dialog.DatePickerDialog;
import com.ros.smartrocket.ui.dialog.RegistrationSuccessDialog;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomSwitch;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.BytesBitmap;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.RegistrationFieldTextWatcher;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.eventbus.AvatarEvent;
import com.ros.smartrocket.utils.image.AvatarImageManager;
import com.ros.smartrocket.utils.image.SelectImageManager;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class RegistrationActivity extends BaseActivity implements RegistrationMvpView, SwitchCheckedChangeListener {
    private static final String STATE_PHOTO = "com.ros.smartrocket.RegistrationActivity.STATE_PHOTO";

    @BindView(R.id.profilePhotoImageView)
    ImageView profilePhotoImageView;
    @BindView(R.id.firstNameEditText)
    CustomEditTextView fullNameEditText;
    @BindView(R.id.maleRadioButton)
    RadioButton maleRadioButton;
    @BindView(R.id.femaleRadioButton)
    RadioButton femaleRadioButton;
    @BindView(R.id.genderRadioGroup)
    RadioGroup genderRadioGroup;
    @BindView(R.id.birthdayEditText)
    CustomEditTextView birthdayEditText;
    @BindView(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @BindView(R.id.emailValidationText)
    CustomTextView emailValidationText;
    @BindView(R.id.passwordEditText)
    CustomEditTextView passwordEditText;
    @BindView(R.id.passwordValidationText)
    CustomTextView passwordValidationText;
    @BindView(R.id.showPasswordToggleButton)
    CustomSwitch showPasswordToggleButton;

    private RegistrationMvpPresenter<RegistrationMvpView> presenter;
    private Registration registrationEntity = new Registration();
    private Long selectedBirthDay = null;
    private Bitmap photoBitmap;
    private File mCurrentPhotoFile;
    private AvatarImageManager avatarImageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        initUI();
        handleArgs();
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_PHOTO)) {
            mCurrentPhotoFile = (File) savedInstanceState.getSerializable(STATE_PHOTO);
        }
    }

    private void initUI() {
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkDeviceSettingsByOnResume(false);
        avatarImageManager = new AvatarImageManager();
        showPasswordToggleButton.setOnCheckedChangeListener(this);
        presenter = new RegistrationPresenter<>();
        presenter.attachView(this);
    }

    private void handleArgs() {
        if (getIntent() != null) {
            Intent i = getIntent();
            registrationEntity.setDistrictId(i.getIntExtra(Keys.DISTRICT_ID, 0));
            registrationEntity.setCountryId(i.getIntExtra(Keys.COUNTRY_ID, 0));
            registrationEntity.setCityId(i.getIntExtra(Keys.CITY_ID, 0));
            registrationEntity.setLatitude(i.getDoubleExtra(Keys.LATITUDE, 0));
            registrationEntity.setLongitude(i.getDoubleExtra(Keys.LONGITUDE, 0));
            registrationEntity.setGroupCode(i.getStringExtra(Keys.GROUP_CODE));
            registrationEntity.setTermsShowed(true);
            if (i.getIntExtra(Keys.REFERRAL_CASES_ID, 0) > 0)
                registrationEntity.setReferralId(i.getIntExtra(Keys.REFERRAL_CASES_ID, 0));

            if (i.getStringExtra(Keys.PROMO_CODE) != null)
                registrationEntity.setPromoCode(i.getStringExtra(Keys.PROMO_CODE));

            String email = getIntent().getStringExtra(Keys.EMAIL);
            emailEditText.setText(email == null ? "" : email);
        }

    }

    private void addTextWatchers() {
        fullNameEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, fullNameEditText));
        birthdayEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, birthdayEditText));
        emailEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, emailEditText));
        passwordEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this, passwordEditText,
                passwordValidationText));
    }

    @NonNull
    private Registration getRegistrationEntity() {
        registrationEntity.setEmail(emailEditText.getText().toString().trim());
        registrationEntity.setPassword(passwordEditText.getText().toString().trim());
        registrationEntity.setFullName(fullNameEditText.getText().toString().trim());
        if (selectedBirthDay != null)
            registrationEntity.setBirthday(UIUtils.longToString(selectedBirthDay, 2));
        if (photoBitmap != null)
            registrationEntity.setPhotoBase64(BytesBitmap.getBase64String(photoBitmap));

        switch (genderRadioGroup.getCheckedRadioButtonId()) {
            case R.id.maleRadioButton:
                registrationEntity.setGender(1);
                break;
            case R.id.femaleRadioButton:
                registrationEntity.setGender(2);
                break;
            default:
                registrationEntity.setGender(-1);
                break;
        }
        return registrationEntity;
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
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            View view = actionBar.getCustomView();
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.registration_form);
        }
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
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(AvatarEvent event) {
        switch (event.type) {
            case START_LOADING:
                showLoading(false);
                break;
            case IMAGE_COMPLETE:
                if (event.image != null && event.image.bitmap != null) {
                    RegistrationActivity.this.photoBitmap = event.image.bitmap;
                    profilePhotoImageView.setImageBitmap(event.image.bitmap);
                } else {
                    profilePhotoImageView.setImageResource(R.drawable.btn_camera_error_selector);
                }
                hideLoading();
                break;
            case SELECT_IMAGE_ERROR:
                hideLoading();
                DialogUtils.showPhotoCanNotBeAddDialog(RegistrationActivity.this);
                break;
        }
    }

    @Override
    public void onCheckedChange(CustomSwitch customSwitch, boolean isChecked) {
        String text = passwordEditText.getText().toString().trim();
        if (isChecked) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        passwordEditText.setSelection(text.length());
        passwordEditText.setTypeface(FontUtils.loadFontFromAsset(getAssets(), FontUtils.getFontAssetPath(2)));
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        if (networkError.getErrorCode() == NetworkError.USER_ALREADY_EXIST_ERROR_CODE) {
            DialogUtils.showUserAlreadyExistDialog(this);
            setEmailFieldState(false);
        } else {
            UIUtils.showSimpleToast(this, networkError.getErrorMessageRes(), Toast.LENGTH_LONG, Gravity.BOTTOM);
            setEmailFieldState(true);
        }
    }

    private void setEmailFieldState(boolean isValidState) {
        UIUtils.setEditTextColorByState(this, emailEditText, isValidState);
        UIUtils.setEmailEditTextImageByState(emailEditText, isValidState);
        emailValidationText.setVisibility(isValidState ? View.GONE : View.VISIBLE);
    }

    @OnClick({R.id.profilePhotoImageView, R.id.maleRadioButton, R.id.femaleRadioButton, R.id.birthdayEditText, R.id.confirmButton, R.id.cancelButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.profilePhotoImageView:
                mCurrentPhotoFile = SelectImageManager.getTempFile(this, SelectImageManager.PREFIX_PROFILE);
                avatarImageManager.showSelectImageDialog(this, true, mCurrentPhotoFile);
                break;
            case R.id.maleRadioButton:
            case R.id.femaleRadioButton:
                UIUtils.setRadioButtonsByState(maleRadioButton, femaleRadioButton, true);
                break;
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
            case R.id.confirmButton:
                fullNameEditText.clearFocus();
                emailEditText.clearFocus();
                passwordEditText.clearFocus();
                presenter.register(getRegistrationEntity());
                addTextWatchers();
                break;
            case R.id.cancelButton:
                finish();
                break;
        }
    }

    @Override
    public void notValidEmail() {
        UIUtils.setEditTextColorByState(this, emailEditText, false);
        UIUtils.setEmailEditTextImageByState(emailEditText, false);
        emailEditText.requestFocus();
        UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);
    }

    @Override
    public void notValidName() {
        UIUtils.setEditTextColorByState(this, fullNameEditText, false);
        fullNameEditText.requestFocus();
        UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);
    }

    @Override
    public void notValidPassword() {
        UIUtils.setEditTextColorByState(this, passwordEditText, false);
        UIUtils.setPasswordEditTextImageByState(passwordEditText, false);
        passwordValidationText.setVisibility(View.VISIBLE);
        passwordEditText.requestFocus();
        UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);
    }

    @Override
    public void notValidBirthday() {
        UIUtils.setEditTextColorByState(this, birthdayEditText, selectedBirthDay != null);
        fullNameEditText.requestFocus();
        fullNameEditText.clearFocus();
        UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);
    }

    @Override
    public void notValidGender() {
        UIUtils.setRadioButtonsByState(maleRadioButton, femaleRadioButton,
                false);
        UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);
    }

    @Override
    public void onRegistrationSuccess() {
        PreferencesManager.getInstance().setTandCShowed(emailEditText.getText().toString().trim());
        new RegistrationSuccessDialog(this, emailEditText.getText().toString().trim());
    }
}
