package com.ros.smartrocket.ui.login.password.forgot;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.ui.base.BaseActivity;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends BaseActivity implements ForgotPassMvpView {
    @BindView(R.id.mailImageView)
    ImageView mailImageView;
    @BindView(R.id.emailEditText)
    CustomEditTextView emailEditText;
    private ForgotPassMvpPresenter<ForgotPassMvpView> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        initUI();
        presenter = new ForgotPassPresenter<>();
        presenter.attachView(this);
    }

    private void initUI() {
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.orange));
        checkDeviceSettingsByOnResume(false);
    }

    @OnClick({R.id.sendButton, R.id.cancelButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                sendRequest();
                break;
            case R.id.cancelButton:
                finish();
                break;
        }
    }

    private void sendRequest() {
        String email = emailEditText.getText().toString().trim();
        UIUtils.setEditTextColorByState(this, emailEditText, UIUtils.isEmailValid(email));
        UIUtils.setEmailImageByState(mailImageView, UIUtils.isEmailValid(email));
        presenter.restorePassword(email);
    }

    @Override
    public void onRequestSuccess() {
        startActivity(IntentUtils.getForgotPasswordSuccessIntent(this, emailEditText.getText().toString().trim()));
        finish();
    }

    @Override
    public void onFieldsEmpty() {
        UIUtils.showSimpleToast(this, R.string.fill_in_field);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
    }
}
