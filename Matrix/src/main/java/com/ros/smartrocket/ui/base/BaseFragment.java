package com.ros.smartrocket.ui.base;

import android.support.v4.app.Fragment;

import com.ros.smartrocket.ui.dialog.CustomProgressDialog;

public class BaseFragment extends Fragment implements MvpView {
    private CustomProgressDialog progressDialog;

    @Override
    public void showLoading() {
        progressDialog = CustomProgressDialog.show(getActivity());
        progressDialog.setCancelable(false);
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
