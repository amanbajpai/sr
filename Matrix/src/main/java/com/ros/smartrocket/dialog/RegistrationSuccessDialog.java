package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.IntentUtils;

public class RegistrationSuccessDialog extends Dialog implements View.OnClickListener {
    //private static final String TAG = RegistrationSuccessDialog.class.getSimpleName();
    private Activity activity;

    public RegistrationSuccessDialog(Activity activity, String email) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.dialog_registration_success);
        setCancelable(false);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView) findViewById(R.id.email)).setText(email);

        findViewById(R.id.okButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                activity.startActivity(IntentUtils.getLoginIntentForLogout(activity));
                dismiss();
                break;
            default:
                break;
        }
    }
}
