package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentInfoDialog extends Dialog {
    private static final String TAG = ActivityLogDialog.class.getSimpleName();

    public PaymentInfoDialog(Activity activity) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_payment_info);
        ButterKnife.bind(this);
        setCancelable(true);

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @OnClick(R.id.okButton)
    public void onViewClicked() {
        dismiss();
    }
}

