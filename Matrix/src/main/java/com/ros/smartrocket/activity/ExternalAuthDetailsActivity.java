package com.ros.smartrocket.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.dialog.DatePickerDialog;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomEditTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExternalAuthDetailsActivity extends BaseActivity {
    public static final int REQUEST_CODE = 2017;
    public static final String EXTERNAL_AUTHORIZE = "externalAuthorize";
    public static final String BITMASK = "bitmask";
    public static final int EMAIL_MASK = 1;
    public static final int BIRTH_MASK = 2;
    @Bind(R.id.emailEditText)
    CustomEditTextView emailEditText;
    @Bind(R.id.birthdayEditText)
    CustomEditTextView birthdayEditText;
    @Bind(R.id.continue_btn)
    CustomButton continueWithEmailBtn;
    private int bitmasc;
    private ExternalAuthorize externalAuthorize;
    private Long selectedBirthDay = null;

    public static Intent getStartIntent(Activity activity, ExternalAuthorize externalAuthorize, int bitmask) {
        Intent i = new Intent(activity, ExternalAuthDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTERNAL_AUTHORIZE, externalAuthorize);
        bundle.putInt(BITMASK, bitmask);
        i.putExtras(bundle);
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_extaernal_registration_details);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bitmasc = bundle.getInt(BITMASK);
            externalAuthorize = (ExternalAuthorize) bundle.getSerializable(EXTERNAL_AUTHORIZE);
            initUI();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private void initUI() {
        birthdayEditText.setVisibility(isBirthdayNeeded() ? View.VISIBLE : View.GONE);
        emailEditText.setVisibility(isEmailNeeded() ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.birthdayEditText, R.id.continue_btn})
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
                    externalAuthorize.setBirthday(UIUtils.longToString(selectedBirthDay, 2));
                    externalAuthorize.setEmail(email);
                    Intent i = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(EXTERNAL_AUTHORIZE, externalAuthorize);
                    setResult(RESULT_OK, i);
                    finish();
                }
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
        return (bitmasc & EMAIL_MASK) == EMAIL_MASK;
    }

    public boolean isBirthdayNeeded() {
        return (bitmasc & BIRTH_MASK) == BIRTH_MASK;
    }
}
