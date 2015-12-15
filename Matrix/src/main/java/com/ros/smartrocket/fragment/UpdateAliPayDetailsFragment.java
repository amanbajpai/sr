package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
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
import com.ros.smartrocket.utils.ValidationUtils;

/**
 * Created by macbook on 02.10.15.
 */
public class UpdateAliPayDetailsFragment extends Fragment implements NetworkOperationListenerInterface, View.OnClickListener {

    private APIFacade apiFacade = APIFacade.getInstance();
    private EditText loginEditText, userIdEditText;
    private Button loginButton;
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
        userIdEditText = (EditText) view.findViewById(R.id.userIdEditText);

        loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        if (App.getInstance().getMyAccount().getAliPayAccountExists()) {
            apiFacade.getAliPayAccount(getActivity());
            startProgress();
            if (BuildConfig.DEBUG) {
                Toast.makeText(getActivity(), getResources().getString(R.string.fetching_alipay_account_info), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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

        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_ALIPAY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                AliPayAccount aliPayAccount = (AliPayAccount) operation.getResponseEntities().get(0);
                loginEditText.setText(aliPayAccount.getAccName());
                userIdEditText.setText(aliPayAccount.getUserId());
                clearProgress();
            } else if (Keys.INTEGRATE_ALIPAY_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                clearProgress();
                Toast.makeText(getActivity(), getResources().getString(R.string.alipay_account_integrated_successfully), Toast.LENGTH_LONG).show();
                MyAccount myAccount = App.getInstance().getMyAccount();
                myAccount.setAliPayAccountExists(true);
                App.getInstance().setMyAccount(myAccount);
                getActivity().finish();
            }
        } else {
            clearProgress();
            Toast.makeText(getActivity(), operation.getResponseError(), Toast.LENGTH_LONG).show();
            loginEditText.setFocusableInTouchMode(true);
            userIdEditText.setFocusableInTouchMode(true);
            loginButton.setEnabled(true);
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
                    AliPayAccount aliPayAccount = new AliPayAccount();
                    aliPayAccount.setAccName(loginEditText.getText().toString());
                    aliPayAccount.setUserId(userIdEditText.getText().toString());
                    apiFacade.integrateAliPayAccount(getActivity(), aliPayAccount);
                }
                break;
        }
    }

    private boolean validateFields() {
        boolean ok = true;
        if (loginEditText.getText().toString().trim().isEmpty()) {
            loginEditText.setError(getResources().getString(R.string.enter_valid_alipay_username));
            ok = false;
        }
        if (!(ValidationUtils.validEmail(userIdEditText.getText().toString())
                || ValidationUtils.validChinaPhone(userIdEditText.getText().toString()))) {
            userIdEditText.setError(getResources().getString(R.string.enter_valid_email_phone));
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
