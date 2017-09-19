package com.ros.smartrocket.presentation.cash.payment.alipay;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.ui.views.CustomEditTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UpdateAliPayDetailsFragment extends BaseFragment implements AliPayMvpView {

    @BindView(R.id.loginEditText)
    CustomEditTextView loginEditText;
    @BindView(R.id.userIdEditText)
    CustomEditTextView userIdEditText;
    Unbinder unbinder;

    private ImageView refreshButton;
    private AliPayMvpPresenter<AliPayMvpView> presenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_update_alipay_detils, null);
        unbinder = ButterKnife.bind(this, view);
        presenter = new AliPayPresenter<>();
        presenter.attachView(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        initRefreshButton();
        getAliPayAccount();
    }

    private void getAliPayAccount() {
        if (App.getInstance().getMyAccount().getIsPaymentAccountExists())
            presenter.getAliPayAccount();
    }

    private void initUI() {
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_all_task);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);

            View actionBarCustomView = actionBar.getCustomView();
            ((TextView) actionBarCustomView.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_payment_details);
            actionBarCustomView.findViewById(R.id.starButton).setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initRefreshButton() {
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (refreshButton == null && actionBar != null) {
            View view = actionBar.getCustomView();
            if (view != null) {
                refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
                if (refreshButton != null) {
                    refreshButton.setOnClickListener(__ -> getAliPayAccount());
                }
            }
        }
    }

    @Override
    public void onStop() {
        presenter.detachView();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.attachView(this);
    }

    @Override
    public void startProgress() {
        refreshButton.setVisibility(View.VISIBLE);
        refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
    }

    @Override
    public void clearProgress() {
        refreshButton.setVisibility(View.GONE);
        refreshButton.clearAnimation();
    }

    @Override
    public void notValidName() {
        loginEditText.setError(getResources().getString(R.string.enter_valid_alipay_username));
    }

    @Override
    public void notValidUserId() {
        userIdEditText.setError(getResources().getString(R.string.enter_valid_email_phone));
    }

    @Override
    public void onAccountLoaded(AliPayAccount account) {
        loginEditText.setText(account.getAccName());
        userIdEditText.setText(account.getUserId());
    }

    @Override
    public void onAccountIntegrated() {
        Toast.makeText(getActivity(), getResources().getString(R.string.alipay_account_integrated_successfully), Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        Toast.makeText(getActivity(), networkError.getErrorMessageRes(), Toast.LENGTH_LONG).show();
        loginEditText.setFocusableInTouchMode(true);
        userIdEditText.setFocusableInTouchMode(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.saveBtn)
    public void onViewClicked() {
        AliPayAccount aliPayAccount = new AliPayAccount();
        aliPayAccount.setAccName(loginEditText.getText().toString());
        aliPayAccount.setUserId(userIdEditText.getText().toString());
        presenter.integrateAliPayAccount(aliPayAccount);
    }
}
