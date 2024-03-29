package com.ros.smartrocket.presentation.cash;

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
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.account.base.AccountMvpPresenter;
import com.ros.smartrocket.presentation.account.base.AccountMvpView;
import com.ros.smartrocket.presentation.account.base.AccountPresenter;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.ui.dialog.PaymentInfoDialog;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CashingOutFragment extends BaseFragment implements AccountMvpView {
    @BindView(R.id.updatePaymentBtn)
    CustomButton updatePaymentBtn;
    @BindView(R.id.currentBalance)
    CustomTextView currentBalance;
    @BindView(R.id.cashOutButton)
    CustomButton cashOutButton;
    @BindView(R.id.minBalance)
    CustomTextView minBalance;
    @BindView(R.id.paymentInProgress)
    CustomTextView paymentInProgress;

    private MyAccount myAccount;
    private AccountMvpPresenter<AccountMvpView> accountPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        ViewGroup view = (ViewGroup) localInflater.inflate(R.layout.fragment_cashing_out, null);
        ButterKnife.bind(this, view);
        initPresenters();
        return view;
    }

    private void initPresenters() {
        accountPresenter = new AccountPresenter<>(true);
        accountPresenter.attachView(this);
        //accountPresenter.getAccount();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View view = actionBar.getCustomView();
            ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_title);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void startEditPaymentInfo() {
        getActivity().startActivity(IntentUtils.getMyAccountIntent(getActivity()));
    }

    @OnClick({R.id.cashOutButton, R.id.updatePaymentBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cashOutButton:
                if (myAccount.canWithdraw())
                    getActivity().startActivity(IntentUtils.getCashOutConfirmationIntent(getActivity()));
                else
                    new PaymentInfoDialog(getActivity());
                break;
            case R.id.updatePaymentBtn:
                startEditPaymentInfo();
                break;
        }
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(getActivity(), networkError.getErrorMessageRes());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accountPresenter != null) {
            accountPresenter.getAccount();
        }


    }

    @Override
    public void onAccountLoaded(MyAccount account) {
        myAccount = account;
        if (myAccount.isWithdrawEnabled()) cashOutButton.setEnabled(true);

        if (myAccount.getBalance() < myAccount.getMinimalWithdrawAmount()) {
            minBalance.setVisibility(View.VISIBLE);
            minBalance.setText(getActivity().getString(R.string.cashing_out_minimum_balance,
                    UIUtils.getBalanceOrPrice(myAccount.getMinimalWithdrawAmount(),
                            myAccount.getCurrencySign(), 2, BigDecimal.ROUND_DOWN)
            ));
        }

        if (myAccount.getInPaymentProcess() > 0) {
            paymentInProgress.setVisibility(View.VISIBLE);
            paymentInProgress.setText(getActivity().getString(R.string.cashing_out_payment_in_progress,
                    UIUtils.getBalanceOrPrice(myAccount.getInPaymentProcess(), myAccount.getCurrencySign())
            ));
        }

        currentBalance.setText(UIUtils.getBalanceOrPrice(myAccount.getBalance(),
                myAccount.getCurrencySign(), 2, BigDecimal.ROUND_DOWN));
    }

    @Override
    public void onStop() {
        accountPresenter.detachView();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        accountPresenter.attachView(this);
    }
}
