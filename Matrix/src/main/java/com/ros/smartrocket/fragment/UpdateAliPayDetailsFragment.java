package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.db.entity.AliPayAccount;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Created by macbook on 02.10.15.
 */
public class UpdateAliPayDetailsFragment extends Fragment implements NetworkOperationListenerInterface, View.OnClickListener {

    private APIFacade apiFacade = APIFacade.getInstance();
    private ViewGroup view;
    private EditText loginEditText, phoneEditText, smsEditText;
    private Button loginButton, sendCodeButton;
    private ImageView refreshButton;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_update_alipay_detils, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_all_task);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View actionBarCustomView = actionBar.getCustomView();
        ((TextView) actionBarCustomView.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_payment_details);
        initRefreshButton();

        loginEditText = (EditText) view.findViewById(R.id.loginEditText);
        phoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        smsEditText = (EditText) view.findViewById(R.id.codeEditText);
        smsEditText.setFocusable(false);

        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        sendCodeButton = (Button) view.findViewById(R.id.sendCodeButton);
        sendCodeButton.setOnClickListener(this);
        sendCodeButton.setEnabled(false);

        if (App.getInstance().getMyAccount().getAliPayAccountExists()) {
            apiFacade.getAliPayAccount(getActivity());
            startProgress();
            if (BuildConfig.DEBUG) {
                Toast.makeText(getActivity(), "Fetching AliPay Account info...", Toast.LENGTH_LONG).show();
            }
        }

//        if (BuildConfig.DEBUG) {
//            loginEditText.setText("jackjmcg@gmail.com");
//            phoneEditText.setText("13681846165");
//        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();

//        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
//        actionBar.setCustomView(R.layout.actionbar_custom_view_all_task);
//        actionBar.setDisplayShowTitleEnabled(false);
//        actionBar.setDisplayShowCustomEnabled(true);
//
//        View view = actionBar.getCustomView();
//        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_payment_details);
//        initRefreshButton();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void initRefreshButton() {
        if (refreshButton == null) {
            final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            View view = actionBar.getCustomView();
            if (view != null) {
                refreshButton = (ImageView) view.findViewById(R.id.refreshButton);
                if (refreshButton != null) {
                    refreshButton.setOnClickListener(this);
                }
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
//        if (Keys.GET_MY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
//            ((CashingOutActivity) getActivity()).setSupportProgressBarIndeterminateVisibility(false);

        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_ALIPAY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                AliPayAccount aliPayAccount = (AliPayAccount) operation.getResponseEntities().get(0);
                loginEditText.setText(aliPayAccount.getAccName());
                phoneEditText.setText(aliPayAccount.getPhone());
                clearProgress();
            } else if (Keys.SEND_ALIPAY_SMS_OPERATION_TAG.equals(operation.getTag())) {
                UIUtils.showSimpleToast(getActivity(), getResources().getString(R.string.update_alipay_wait_for_sms));
                clearProgress();
                sendCodeButton.setEnabled(true);
                smsEditText.setFocusableInTouchMode(true);
            } else if (Keys.INTEGRATE_ALIPAY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                clearProgress();
                UIUtils.showSimpleToast(getActivity(), "AliPay Account integrated successfully!!");
                MyAccount myAccount = App.getInstance().getMyAccount();
                myAccount.setAliPayAccountExists(true);
                App.getInstance().setMyAccount(myAccount);
                getActivity().finish();
            }
        } else {
            clearProgress();
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
            loginEditText.setFocusableInTouchMode(true);
            phoneEditText.setFocusableInTouchMode(true);
            loginButton.setEnabled(true);
            sendCodeButton.setEnabled(false);
            smsEditText.setFocusable(false);
            smsEditText.setText("");
        }
//        }
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                if (validateFields()) {
                    apiFacade.sendAliPaySms(getActivity(), phoneEditText.getText().toString());
                    startProgress();
                    loginEditText.setFocusable(false);
                    phoneEditText.setFocusable(false);
                    loginButton.setEnabled(false);
                }
                break;
            case R.id.sendCodeButton:
                if (smsEditText.getText().toString().isEmpty()) {
                    smsEditText.setError("Required Field");
                } else {
                    AliPayAccount aliPayAccount = new AliPayAccount();
                    aliPayAccount.setAccName(loginEditText.getText().toString());
                    aliPayAccount.setPhone(phoneEditText.getText().toString());
                    aliPayAccount.setSmsCode(smsEditText.getText().toString());
                    apiFacade.integrateAliPayAccount(getActivity(), aliPayAccount);
                }

                break;
        }
    }

    private boolean validateFields() {
        boolean ok = true;
        if (loginEditText.getText().toString().isEmpty()) {
            loginEditText.setError("Required Field");
            ok = false;
        }
        if (phoneEditText.getText().toString().isEmpty()) {
            phoneEditText.setError("Required Field");
            ok = false;
        }
        return ok;

    }

    private void startProgress() {
        refreshButton.setVisibility(View.VISIBLE);
        refreshButton.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
    }

    private void clearProgress() {
        refreshButton.setVisibility(View.GONE);
        refreshButton.clearAnimation();
    }
}
