package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import com.ros.smartrocket.R;

/**
 * Dialog for notify user, that ID Card is supported
 */
public class UpdateFirstLastNameDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = UpdateFirstLastNameDialog.class.getSimpleName();
    private DialogButtonClickListener buttonClickListener;

    public UpdateFirstLastNameDialog(Activity activity, DialogButtonClickListener buttonClickListener) {
        super(activity);
        this.buttonClickListener = buttonClickListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_update_first_last_name);
        setCancelable(false);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.updateButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                buttonClickListener.onCancelButtonPressed();
                break;
            case R.id.updateButton:
                dismiss();
                buttonClickListener.onUpdateButtonPressed();
                break;
            default:
                break;
        }
    }

    public interface DialogButtonClickListener {
        void onCancelButtonPressed();

        void onUpdateButtonPressed();
    }
}
