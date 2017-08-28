package com.ros.smartrocket.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.ui.views.CustomTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateVersionDialog extends Dialog {
    private static final String TAG = ActivityLogDialog.class.getSimpleName();
    @BindView(R.id.dialogText)
    CustomTextView dialogText;
    private DialogButtonClickListener dialogButtonClickListener;

    public UpdateVersionDialog(Activity activity, String currentVersion, String latestVersion,
                               DialogButtonClickListener dialogButtonClickListener) {
        super(activity);
        this.dialogButtonClickListener = dialogButtonClickListener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_update_version);
        ButterKnife.bind(this);
        setCancelable(true);
        dialogText.setText(getContext().getString(R.string.dialog_update_app_text, currentVersion, latestVersion));
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

    }

    @OnClick({R.id.cancelButton, R.id.okButton})
    public void onClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.cancelButton:
                dialogButtonClickListener.onCancelButtonPressed();
                break;
            case R.id.okButton:
                dialogButtonClickListener.onOkButtonPressed();
                break;
        }
    }

    public interface DialogButtonClickListener {
        void onCancelButtonPressed();
        void onOkButtonPressed();
    }
}