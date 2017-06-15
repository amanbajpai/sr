package com.ros.smartrocket.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.dialog.DatePickerDialog;
import com.ros.smartrocket.dialog.RegistrationSuccessDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomEditTextView;
import com.ros.smartrocket.views.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExternalAuthDetailsActivity extends BaseActivity implements NetworkOperationListenerInterface {
    public static final String EXTERNAL_AUTHORIZE = "externalAuthorize";
    public static final String BITMASK = "bitmask";
    public static final int EMAIL_MASK = 1;
    public static final int BIRTH_MASK = 2;
    @BindView(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @BindView(R.id.birthdayEditText)
    CustomEditTextView birthdayEditText;
    @BindView(R.id.txt_why_dob)
    CustomTextView txtWhyDob;
    @BindView(R.id.birthLayout)
    LinearLayout birthLayout;
    @BindView(R.id.txt_why_email)
    CustomTextView txtWhyEmail;
    @BindView(R.id.emailLayout)
    LinearLayout emailLayout;
    private int bitMasc;
    private ExternalAuthorize externalAuthorize;
    private Long selectedBirthDay = null;
    private APIFacade apiFacade = APIFacade.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_extaernal_registration_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bitMasc = bundle.getInt(BITMASK);
            externalAuthorize = (ExternalAuthorize) bundle.getSerializable(EXTERNAL_AUTHORIZE);
            initUI();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void initUI() {
        birthLayout.setVisibility(isBirthdayNeeded() ? View.VISIBLE : View.GONE);
        emailLayout.setVisibility(isEmailNeeded() ? View.VISIBLE : View.GONE);
        txtWhyDob.setPaintFlags(txtWhyDob.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtWhyEmail.setPaintFlags(txtWhyEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick({R.id.birthdayEditText, R.id.continue_btn, R.id.txt_why_dob, R.id.txt_why_email})
    public void onClick(View view) {
        switch (view.getId()) {
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
            case R.id.continue_btn:
                String email = emailEditText.getText().toString().trim();
                UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
                UIUtils.setEditTextColorByState(this, birthdayEditText, selectedBirthDay != null);
                if (!isAllFieldsFilled(email)) {
                    UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);
                } else {
                    showProgressDialog(false);
                    if (selectedBirthDay != null) {
                        externalAuthorize.setBirthday(UIUtils.longToString(selectedBirthDay, 2));
                    }
                    if (!email.isEmpty()) {
                        externalAuthorize.setEmail(email);
                    }
                    int referralCasesId = getIntent().getIntExtra(Keys.REFERRAL_CASES_ID, 0);
                    if (referralCasesId > 0) {
                        externalAuthorize.setReferralId(referralCasesId);
                    }
                    String promoCode = getIntent().getStringExtra(Keys.PROMO_CODE);
                    if (!TextUtils.isEmpty(promoCode)) {
                        externalAuthorize.setPromoCode(promoCode);
                    }
                    apiFacade.externalRegistration(this, externalAuthorize);
                }
                break;
            case R.id.txt_why_dob:
                DialogUtils.showWhyWeNeedThisDialog(this, R.string.why_dob_title, R.string.why_bod);
                break;
            case R.id.txt_why_email:
                DialogUtils.showWhyWeNeedThisDialog(this, R.string.why_email_title, R.string.why_email);
                break;
        }
    }

    private boolean isAllFieldsFilled(String email) {
        boolean result = !isEmailNeeded() || !email.isEmpty();
        result &= !isBirthdayNeeded() || selectedBirthDay != null;
        return result;
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

    public boolean isEmailNeeded() {
        return (bitMasc & EMAIL_MASK) == EMAIL_MASK;
    }

    public boolean isBirthdayNeeded() {
        return (bitMasc & BIRTH_MASK) == BIRTH_MASK;
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.POST_EXTERNAL_REG_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
                onSuccessNetworkOperation();
            } else {
                onErrorNetworkOperation(operation);
            }
        }
    }

    private void onSuccessNetworkOperation() {
        PreferencesManager.getInstance().setTandCShowed(emailEditText.getText().toString().trim());
        new RegistrationSuccessDialog(this, externalAuthorize.getEmail());
    }

    private void onErrorNetworkOperation(BaseOperation operation) {
        if (operation.getResponseErrorCode() != null) {
            if (operation.getResponseErrorCode() == BaseNetworkService.NO_INTERNET) {
                DialogUtils.showBadOrNoInternetDialog(this);
            } else if (operation.getResponseErrorCode() == BaseNetworkService.USER_NOT_FOUND_ERROR_CODE) {
                DialogUtils.showLoginFailedDialog(this);
            } else {
                showNetworkError(operation);
            }
        } else {
            showNetworkError(operation);
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
