package com.ros.smartrocket.presentation.login.external;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.main.MainActivity;
import com.ros.smartrocket.ui.dialog.DatePickerDialog;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExternalAuthDetailsActivity extends BaseActivity implements ExternalRegistrationMvpView {
    public static final String EXTERNAL_AUTHORIZE = "externalAuthorize";
    public static final String BITMASK = "bitmask";

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

    private Long selectedBirthDay = null;
    private ExternalRegistrationMvpPresenter<ExternalRegistrationMvpView> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHomeAsUp();
        setContentView(R.layout.activity_extaernal_registration_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        fetchArguments(bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void fetchArguments(Bundle bundle) {
        if (bundle != null && bundle.getSerializable(EXTERNAL_AUTHORIZE) != null) {
            ExternalAuthorize externalAuthorize = (ExternalAuthorize) bundle.getSerializable(EXTERNAL_AUTHORIZE);

            int referralCasesId = getIntent().getIntExtra(Keys.REFERRAL_CASES_ID, 0);
            if (referralCasesId > 0) externalAuthorize.setReferralId(referralCasesId);

            String promoCode = getIntent().getStringExtra(Keys.PROMO_CODE);
            if (!TextUtils.isEmpty(promoCode)) externalAuthorize.setPromoCode(promoCode);

            presenter = new ExternalRegistrationPresenter<>(bundle.getInt(BITMASK), externalAuthorize);
            presenter.attachView(this);
            presenter.setUpUI();
            initUI();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void initUI() {
        txtWhyDob.setPaintFlags(txtWhyDob.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtWhyEmail.setPaintFlags(txtWhyEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick({R.id.birthdayEditText, R.id.continue_btn, R.id.txt_why_dob, R.id.txt_why_email})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.birthdayEditText:
                showDatePicker();
                break;
            case R.id.continue_btn:
                String email = emailEditText.getText().toString().trim();
                setStatesForInputs(email);
                presenter.registerExternal(selectedBirthDay, email);
                break;
            case R.id.txt_why_dob:
                DialogUtils.showWhyWeNeedThisDialog(this, R.string.why_dob_title, R.string.why_bod);
                break;
            case R.id.txt_why_email:
                DialogUtils.showWhyWeNeedThisDialog(this, R.string.why_email_title, R.string.why_email);
                break;
        }
    }

    private void setStatesForInputs(String email) {
        UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
        UIUtils.setEditTextColorByState(this, birthdayEditText, selectedBirthDay != null);
    }

    private void showDatePicker() {
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
    public void showNetworkError(BaseNetworkError networkError) {
        switch (networkError.getErrorCode()) {
            case NetworkError.NO_INTERNET:
                DialogUtils.showBadOrNoInternetDialog(this);
                break;
            case NetworkError.USER_NOT_FOUND_ERROR_CODE:
                DialogUtils.showLoginFailedDialog(this);
                break;
            default:
                UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
                break;
        }
    }

    @Override
    public void showDoBField() {
        birthLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmailField() {
        emailLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFieldsEmpty() {
        UIUtils.showSimpleToast(this, R.string.fill_in_all_fields);
    }

    @Override
    public void onRegistrationSuccess(String email) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
