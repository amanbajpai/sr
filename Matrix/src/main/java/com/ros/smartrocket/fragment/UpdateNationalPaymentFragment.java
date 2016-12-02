package com.ros.smartrocket.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomEditTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateNationalPaymentFragment extends Fragment implements NetworkOperationListenerInterface {
    @Bind(R.id.nameEdt)
    CustomEditTextView nameEdt;
    @Bind(R.id.userNationalIdEdt)
    CustomEditTextView userNationalIdEdt;
    @Bind(R.id.phoneEdt)
    CustomEditTextView phoneEdt;
    @Bind(R.id.submitButton)
    CustomButton submitButton;
    private APIFacade apiFacade = APIFacade.getInstance();
    private CustomProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View v = localInflater.inflate(R.layout.fragment_national_id_setup, null);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyAccount account = App.getInstance().getMyAccount();
        if (account != null) {
            nameEdt.setText(account.getSingleName());
        }
        final ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            View actionBarCustomView = actionBar.getCustomView();
            ((TextView) actionBarCustomView.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_payment_details);
        }
        if (App.getInstance().getMyAccount().getIsPaymentAccountExists()) {
            apiFacade.getNationalIdAccount(getActivity());
            startProgress();
        }

    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {

        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_NATIONAL_ID_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                clearProgress();
                NationalIdAccount nationalIdAccount = (NationalIdAccount) operation.getResponseEntities().get(0);
                nameEdt.setText(nationalIdAccount.getName());
                userNationalIdEdt.setText(nationalIdAccount.getNationalId());
                phoneEdt.setText(nationalIdAccount.getPhoneNumber());
            } else if (Keys.INTEGRATE_NATIONAL_ID_ACCOUNT_OPERATION_TAG.equals(operation.getTag())) {
                clearProgress();
                Toast.makeText(getActivity(), getResources().getString(R.string.national_id_account_integrated_successfully), Toast.LENGTH_LONG).show();
                MyAccount myAccount = App.getInstance().getMyAccount();
                myAccount.setIsPaymentAccountExists(true);
                App.getInstance().setMyAccount(myAccount);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } else {
            clearProgress();
            Toast.makeText(getActivity(), operation.getResponseError(), Toast.LENGTH_LONG).show();
            nameEdt.setFocusableInTouchMode(true);
            userNationalIdEdt.setFocusableInTouchMode(true);
            phoneEdt.setFocusableInTouchMode(true);
            submitButton.setEnabled(true);
        }
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
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.submitButton)
    public void onClick() {
        if (validateFields()) {
            startProgress();
            NationalIdAccount nationalIdAccount = new NationalIdAccount();
            nationalIdAccount.setName(nameEdt.getText().toString());
            nationalIdAccount.setNationalId(userNationalIdEdt.getText().toString());
            nationalIdAccount.setPhoneNumber(phoneEdt.getText().toString());
            apiFacade.integrateNationalIdAccount(getActivity(), nationalIdAccount);
        } else {
            UIUtils.showSimpleToast(getActivity(), R.string.fill_in_field);
        }
    }

    private boolean validateFields() {
        if (nameEdt.getText().toString().trim().isEmpty() || userNationalIdEdt.getText().toString().trim().isEmpty()
                || phoneEdt.getText().toString().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private void startProgress() {
        progressDialog = CustomProgressDialog.show(getActivity());
    }

    private void clearProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
