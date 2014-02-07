package com.ros.smartrocket.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.R;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.UIUtils;

/**
 * Profile fragment for current user
 */
public class ProfileFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ViewGroup view;

    private EditText fullNameEditText;
    private EditText passwordEditText;
    private EditText dayEditText;
    private EditText monthEditText;
    private EditText yearEditText;
    private EditText emailEditText;
    private EditText countryEditText;
    private EditText cityEditText;

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
            L.i(TAG, "TODO Move to fragment second time");
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            UIUtils.showSimpleToast(getActivity(), "Success");
        } else {
            UIUtils.showSimpleToast(getActivity(), operation.getResponseError());
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
            default:
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().setTitle(R.string.profile_title);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(false);

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
