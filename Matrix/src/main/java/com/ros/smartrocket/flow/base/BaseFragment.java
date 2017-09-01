package com.ros.smartrocket.flow.base;

import android.support.v4.app.Fragment;

import com.ros.smartrocket.ui.dialog.CustomProgressDialog;

public class BaseFragment extends Fragment implements MvpView {
    private CustomProgressDialog progressDialog;

    @Override
    public void showLoading(boolean isCancelable) {
        hideLoading();
        progressDialog = CustomProgressDialog.show(getActivity());
        progressDialog.setCancelable(isCancelable);
    }

    @Override
    public void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
