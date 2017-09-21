package com.ros.smartrocket.presentation.cash.payment.national;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ros.smartrocket.App;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.MyAccount;
import com.ros.smartrocket.db.entity.NationalIdAccount;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.base.BaseFragment;
import com.ros.smartrocket.ui.views.CustomEditTextView;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateNationalPaymentFragment extends BaseFragment implements NationalPayMvpView {
    @BindView(R.id.nameEdt)
    CustomEditTextView nameEdt;
    @BindView(R.id.userNationalIdEdt)
    CustomEditTextView userNationalIdEdt;
    @BindView(R.id.phoneEdt)
    CustomEditTextView phoneEdt;
    private NationalPayMvpPresenter<NationalPayMvpView> presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.FragmentTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View v = localInflater.inflate(R.layout.fragment_national_id_setup, null);
        ButterKnife.bind(this, v);
        presenter = new NationalPayPresenter<>();
        presenter.attachView(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        if (App.getInstance().getMyAccount().getIsPaymentAccountExists()) {
            presenter.getNationalIdAccount();
        }

    }

    private void initUI() {
        MyAccount account = App.getInstance().getMyAccount();
        if (account != null) {
            nameEdt.setText(account.getSingleName());
        }
        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            View actionBarCustomView = actionBar.getCustomView();
            ((TextView) actionBarCustomView.findViewById(R.id.titleTextView)).setText(R.string.cashing_out_payment_details);
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

    @OnClick(R.id.submitButton)
    public void onClick() {
        NationalIdAccount nationalIdAccount = new NationalIdAccount();
        nationalIdAccount.setName(nameEdt.getText().toString());
        nationalIdAccount.setNationalId(userNationalIdEdt.getText().toString());
        nationalIdAccount.setPhoneNumber(phoneEdt.getText().toString());
        presenter.integrateNationalIdAccount(nationalIdAccount);

    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        Toast.makeText(getActivity(), networkError.getErrorMessageRes(), Toast.LENGTH_LONG).show();
        nameEdt.setFocusableInTouchMode(true);
        userNationalIdEdt.setFocusableInTouchMode(true);
        phoneEdt.setFocusableInTouchMode(true);
    }

    @Override
    public void onAccountLoaded(NationalIdAccount account) {
        nameEdt.setText(account.getName());
        userNationalIdEdt.setText(account.getNationalId());
        phoneEdt.setText(account.getPhoneNumber());
    }

    @Override
    public void onAccountIntegrated() {
        Toast.makeText(getActivity(), getResources().getString(R.string.national_id_account_integrated_successfully), Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onFieldsEmpty() {
        UIUtils.showSimpleToast(getActivity(), R.string.fill_in_field);
    }
}
