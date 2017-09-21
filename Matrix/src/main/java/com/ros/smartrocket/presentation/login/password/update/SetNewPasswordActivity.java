package com.ros.smartrocket.presentation.login.password.update;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.interfaces.SwitchCheckedChangeListener;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.ui.views.CustomSwitch;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.FontUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.RegistrationFieldTextWatcher;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetNewPasswordActivity extends BaseActivity implements NewPassMvpView, SwitchCheckedChangeListener {
    @BindView(R.id.passwordEditText)
    CustomEditTextView passwordEditText;
    @BindView(R.id.showPasswordToggleButton)
    CustomSwitch showPasswordToggleButton;
    @BindView(R.id.passwordValidationText)
    CustomTextView passwordValidationText;
    @BindView(R.id.setPasswordButton)
    CustomButton setPasswordButton;

    private NewPassMvpPresenter<NewPassMvpView> presenter;
    private String email;
    private String token;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
        ButterKnife.bind(this);
        presenter = new NewPassPresenter<>();
        presenter.attachView(this);
        initUI();
        handleArgs();
    }

    private void handleArgs() {
        if (getIntent() != null) {
            email = getIntent().getStringExtra(Keys.EMAIL);
            token = getIntent().getStringExtra(Keys.TOKEN);
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
                setPasswordButton.setEnabled(false);
            }
        }
    }

    private void initUI() {
        hideActionBar();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        showPasswordToggleButton.setOnCheckedChangeListener(this);
        checkDeviceSettingsByOnResume(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onCheckedChange(CustomSwitch customSwitch, boolean isChecked) {
        String text = passwordEditText.getText().toString().trim();
        if (isChecked)
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        else
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEditText.setSelection(text.length());
        passwordEditText.setTypeface(FontUtils.loadFontFromAsset(getAssets(), FontUtils.getFontAssetPath(2)));
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
    }

    @OnClick(R.id.setPasswordButton)
    public void onViewClicked() {
        if (!UIUtils.isOnline(this)) {
            LocaleUtils.setCurrentLanguage();
            DialogUtils.showNetworkDialog(this);
        } else {
            handleRestorePassword();
        }
    }

    private void handleRestorePassword() {
        String password = passwordEditText.getText().toString().trim();
        presenter.changePassword(email, token, password);
    }

    @Override
    public void passwordNotValid() {
        UIUtils.setEditTextColorByState(this, passwordEditText, false);
        UIUtils.setPasswordEditTextImageByState(passwordEditText, false);
        passwordValidationText.setVisibility(View.VISIBLE);
        passwordEditText.addTextChangedListener(new RegistrationFieldTextWatcher(this,
                passwordEditText, passwordValidationText));
    }

    @Override
    public void onPasswordChangeSuccess() {
        UIUtils.showSimpleToast(this, R.string.success);
        startActivity(IntentUtils.getLoginIntentForLogout(this));
    }
}
