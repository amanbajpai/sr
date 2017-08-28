package com.ros.smartrocket.ui.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.interfaces.SwitchCheckedChangeListener;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.RegistrationFieldTextWatcher;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomSwitch;
import com.ros.smartrocket.ui.views.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetNewPasswordActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface, SwitchCheckedChangeListener {
    @BindView(R.id.passwordEditText)
    CustomEditTextView passwordEditText;
    @BindView(R.id.showPasswordToggleButton)
    CustomSwitch showPasswordToggleButton;
    @BindView(R.id.passwordValidationText)
    CustomTextView passwordValidationText;
    @BindView(R.id.setPasswordButton)
    CustomButton setPasswordButton;
    private APIFacade apiFacade = APIFacade.getInstance();
    private String email;
    private String token;

    public SetNewPasswordActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_set_new_password);
        ButterKnife.bind(this);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        setPasswordButton.setOnClickListener(this);
        showPasswordToggleButton.setOnCheckedChangeListener(this);

        checkDeviceSettingsByOnResume(false);

        if (getIntent() != null) {
            email = getIntent().getStringExtra(Keys.EMAIL);
            token = getIntent().getStringExtra(Keys.TOKEN);

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
                setPasswordButton.setEnabled(false);
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setPasswordButton:
                if (!UIUtils.isOnline(this)) {
                    LocaleUtils.setCurrentLanguage();
                    DialogUtils.showNetworkDialog(this);
                } else {

                    String password = passwordEditText.getText().toString().trim();

                    boolean isPasswordValid = UIUtils.isPasswordValid(password);
                    UIUtils.setEditTextColorByState(this, passwordEditText, isPasswordValid);
                    UIUtils.setPasswordEditTextImageByState(passwordEditText, isPasswordValid);
                    passwordValidationText.setVisibility(isPasswordValid ? View.GONE : View.VISIBLE);

                    if (!isPasswordValid) {
                        passwordEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this,
                                passwordEditText, passwordValidationText));
                        break;
                    }

                    showProgressDialog(false);
                    setPasswordButton.setEnabled(false);
                    showProgressDialog(false);
                    apiFacade.setPassword(this, email, token, password);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        if (Keys.SET_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            UIUtils.showSimpleToast(this, R.string.success);
            startActivity(IntentUtils.getLoginIntentForLogout(this));
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        if (Keys.SET_PASSWORD_OPERATION_TAG.equals(operation.getTag())) {
            dismissProgressDialog();
            setPasswordButton.setEnabled(true);
            UIUtils.showSimpleToast(this, operation.getResponseError());
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
}
