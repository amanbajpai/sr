package com.matrix.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.db.entity.SubscriptionResponse;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.UIUtils;

public class ProfileFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    //private static final String TAG = ProfileFragment.class.getSimpleName();
    private ViewGroup view;

    public EditText fullNameEditText;
    public EditText passwordEditText;
    public EditText dayEditText;
    public EditText monthEditText;
    public EditText yearEditText;
    public EditText emailEditText;
    public EditText countryEditText;
    public EditText cityEditText;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_profile, null);

        fullNameEditText = (EditText) view.findViewById(R.id.fullNameEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        dayEditText = (EditText) view.findViewById(R.id.dayEditText);
        monthEditText = (EditText) view.findViewById(R.id.monthEditText);
        yearEditText = (EditText) view.findViewById(R.id.yearEditText);
        emailEditText = (EditText) view.findViewById(R.id.emailEditText);
        countryEditText = (EditText) view.findViewById(R.id.countryEditText);
        cityEditText = (EditText) view.findViewById(R.id.cityEditText);

        view.findViewById(R.id.transferFundsButton).setOnClickListener(this);
        view.findViewById(R.id.changeAccountInfoButton).setOnClickListener(this);
        view.findViewById(R.id.changePasswordButton).setOnClickListener(this);
        view.findViewById(R.id.updateMyLocationButton).setOnClickListener(this);
        view.findViewById(R.id.confirmAndSaveButton).setOnClickListener(this);
        view.findViewById(R.id.cancelButton).setOnClickListener(this);

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (!hidden) {
            //TODO Move to fragment second time
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == 200) {

        } else {
            UIUtils.showSimpleToast(getActivity(), "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transferFundsButton:

                break;
            case R.id.changeAccountInfoButton:

                break;
            case R.id.changePasswordButton:

                break;
            case R.id.updateMyLocationButton:

                break;
            case R.id.confirmAndSaveButton:

                break;
            case R.id.cancelButton:

                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().setTitle(R.string.profile_title);

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
}