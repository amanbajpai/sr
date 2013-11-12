package com.matrix.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.matrix.BaseActivity;
import com.matrix.R;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.UIUtils;

public class AboutMatrixFragment extends Fragment implements OnClickListener, NetworkOperationListenerInterface {
    //private static final String TAG = AboutMatrixFragment.class.getSimpleName();
    private ViewGroup view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (ViewGroup) inflater.inflate(R.layout.fragment_about_matrix, null);

        getActivity().setTitle(R.string.about_matrix_title);

        view.findViewById(R.id.termAndConditionsButton).setOnClickListener(this);
        view.findViewById(R.id.faqButton).setOnClickListener(this);
        view.findViewById(R.id.helpButton).setOnClickListener(this);

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
            //TODO Do something
        } else {
            UIUtils.showSimpleToast(getActivity(), "Server Error. Response Code: " + operation.getResponseStatusCode());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.termAndConditionsButton:

                break;
            case R.id.faqButton:

                break;
            case R.id.helpButton:

                break;
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
}