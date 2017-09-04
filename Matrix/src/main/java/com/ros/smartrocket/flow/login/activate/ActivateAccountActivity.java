package com.ros.smartrocket.flow.login.activate;

import android.os.Bundle;
import android.text.TextUtils;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivateAccountActivity extends BaseActivity implements ActivateMvpView {
    @BindView(R.id.activateAccountButton)
    CustomButton activateAccountButton;

    private String email;
    private String token;
    private ActivateAccMvpPresenter<ActivateMvpView> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);
        ButterKnife.bind(this);
        presenter = new ActivateAccPresenter<>();
        presenter.attachView(this);
        initUI();
        handleArgs();
        checkDeviceSettingsByOnResume(false);
    }

    private void initUI() {
        hideActionBar();
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
    }

    private void handleArgs() {
        if (getIntent() != null) {
            email = getIntent().getStringExtra(Keys.EMAIL);
            token = getIntent().getStringExtra(Keys.TOKEN);
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(token)) {
                activateAccountButton.setEnabled(false);
            }
        }
    }

    @Override
    public void onAccountActivated() {
        DialogUtils.showAccountConfirmedDialog(this);
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
    }

    @OnClick(R.id.activateAccountButton)
    public void onViewClicked() {
        LocaleUtils.setCurrentLanguage();
        if (!UIUtils.isOnline(this)) {
            DialogUtils.showNetworkDialog(this);
        } else {
            presenter.activateAccount(email, token);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
