package com.matrix.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;
import com.matrix.R;
import com.matrix.activity.LoginActivity;

public class RegistrationSuccessDialog extends Dialog implements View.OnClickListener {
    public static final String TAG = RegistrationSuccessDialog.class.getSimpleName();
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
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView) findViewById(R.id.title)).setText(activity.getString(R.string.registration_success_dialog_title));
        ((TextView) findViewById(R.id.email)).setText(email);

        findViewById(R.id.okButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.okButton:
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                dismiss();
                break;
            default:
                break;
        }
    }
}
