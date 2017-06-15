package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.activity.CashingOutActivity;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.dialog.ActivityLogDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomTextView;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Cash out fragment
 */
public class CashingOutFragment extends Fragment implements NetworkOperationListenerInterface {
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
    @BindView(R.id.bntDivider)
    View bntDivider;
    private APIFacade apiFacade = APIFacade.getInstance();
    private MyAccount myAccount;

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
        ((CashingOutActivity) getActivity()).showProgressDialog(false);
        apiFacade.getMyAccount(getActivity());
        return view;
    }

    public void updateData() {
        myAccount = App.getInstance().getMyAccount();
        if (myAccount.isPaymentSettingsEnabled()) {
            updatePaymentBtn.setVisibility(View.VISIBLE);
        } else {
            updatePaymentBtn.setVisibility(View.GONE);
            bntDivider.setVisibility(View.GONE);
        }

        if (myAccount.isWithdrawEnabled()) {
            cashOutButton.setEnabled(true);
        }

        if (myAccount.getBalance() < myAccount.getMinimalWithdrawAmount()) {
            minBalance.setVisibility(View.VISIBLE);
            minBalance.setText(getActivity().getString(R.string.cashing_out_minimum_balance,
                    UIUtils.getBalanceOrPrice(myAccount.getMinimalWithdrawAmount(),
                            myAccount.getCurrencySign(), 0, BigDecimal.ROUND_DOWN)
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
    public void onNetworkOperation(BaseOperation operation) {
        ((CashingOutActivity) getActivity()).dismissProgressDialog();
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_MY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                updateData();
            } else if (Keys.SEND_ACTIVITY_OPERATION_TAG.equals(operation.getTag())) {
                if (PreferencesManager.getInstance().getShowActivityDialog()) {
                    new ActivityLogDialog(getActivity(), PreferencesManager.getInstance().getLastEmail());
                } else {
                    UIUtils.showSimpleToast(getActivity(), getString(R.string.activity_log_description_toast)
                            + PreferencesManager.getInstance().getLastEmail());
                }
            }
        } else {
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((BaseActivity) getActivity()).addNetworkOperationListener(this);
    }

    @Override
    public void onStop() {
        ((BaseActivity) getActivity()).removeNetworkOperationListener(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void startEditPaymentInfo() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, myAccount.isAliPay()
                ? new UpdateAliPayDetailsFragment()
                : new UpdateNationalPaymentFragment());
        fragmentTransaction.addToBackStack(myAccount.isAliPay()
                ? UpdateAliPayDetailsFragment.class.getSimpleName() :
                UpdateNationalPaymentFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @OnClick({R.id.cashOutButton, R.id.updatePaymentBtn, R.id.activityBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cashOutButton:
                if (myAccount.canWithdraw()) {
                    getActivity().startActivity(IntentUtils.getCashOutConfirmationIntent(getActivity()));
                } else {
                    startEditPaymentInfo();
                }
                break;
            case R.id.updatePaymentBtn:
                startEditPaymentInfo();
                break;
            case R.id.activityBtn:
                ((CashingOutActivity) getActivity()).showProgressDialog(false);
                apiFacade.sendActivity(getActivity());
                break;
        }
    }
}
