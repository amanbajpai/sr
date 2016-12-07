package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomCheckBox;
import com.ros.smartrocket.views.CustomTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityLogDialog extends Dialog {
    private static final String TAG = BookTaskSuccessDialog.class.getSimpleName();
    @Bind(R.id.emailTxt)
    CustomTextView emailTxt;
    @Bind(R.id.rememberMeCheckBox)
    CustomCheckBox rememberMeCheckBox;

    public ActivityLogDialog(Activity activity, String email) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_activity_log);
        ButterKnife.bind(this);
        setCancelable(true);

        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        emailTxt.setText(email);
    }

    @OnClick(R.id.okButton)
    public void onClick() {
        dismiss();
        if (rememberMeCheckBox.isChecked()) {
            PreferencesManager.getInstance().setShowActivityDialog(false);
        }
    }
}