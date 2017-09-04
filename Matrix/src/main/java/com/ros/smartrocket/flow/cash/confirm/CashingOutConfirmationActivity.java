package com.ros.smartrocket.flow.cash.confirm;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CashingOutConfirmationActivity extends BaseActivity implements ConfirmCashingOutMvpView {
    private ConfirmCashingOutMvpPresenter<ConfirmCashingOutMvpView> presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashing_out_confirmation);
        ButterKnife.bind(this);
        presenter = new ConfirmCashingOutPresenter<>();
        presenter.attachView(this);
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
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
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_title);
        }
        return true;
    }

    @OnClick({R.id.cancelButton, R.id.continueButton})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                finish();
                break;
            case R.id.continueButton:
                presenter.cashingOut();
                break;
        }
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
    }

    @Override
    public void onCashOutSuccess() {
        startActivity(IntentUtils.getCashOutSuccessIntent(this));
        finish();
    }

    @Override
    public void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }
}