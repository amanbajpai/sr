package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;

public class WithdrawTaskDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = WithdrawTaskDialog.class.getSimpleName();
    private DialogButtonClickListener buttonClickListener;

    public WithdrawTaskDialog(Activity activity, String dateTime, DialogButtonClickListener buttonClickListener) {
        super(activity);
        this.buttonClickListener = buttonClickListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_withdraw_task);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView) findViewById(R.id.dateTime)).setText(dateTime);

        findViewById(R.id.noButton).setOnClickListener(this);
        findViewById(R.id.yesButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.noButton:
                dismiss();
                buttonClickListener.onNoButtonPressed(this);
                break;
            case R.id.yesButton:
                dismiss();
                buttonClickListener.onYesButtonPressed(this);
                break;
            default:
                break;
        }
    }

    public interface DialogButtonClickListener {
        void onNoButtonPressed(Dialog dialog);

        void onYesButtonPressed(Dialog dialog);
    }
}
