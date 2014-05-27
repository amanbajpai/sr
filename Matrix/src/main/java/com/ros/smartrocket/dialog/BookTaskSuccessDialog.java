package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;

public class BookTaskSuccessDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = BookTaskSuccessDialog.class.getSimpleName();
    //private Activity activity;
    private DialogButtonClickListener buttonClickListener;

    public BookTaskSuccessDialog(Activity activity, String dateTime, DialogButtonClickListener buttonClickListener) {
        super(activity);
        //this.activity = activity;
        this.buttonClickListener = buttonClickListener;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_book_task_success);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView) findViewById(R.id.text0)).setText(Html.fromHtml(activity.getString(R.string.book_task_success_dialog_text0,
                dateTime)));

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.startLaterButton).setOnClickListener(this);
        findViewById(R.id.startNowButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                buttonClickListener.onCancelButtonPressed(this);
                break;
            case R.id.startLaterButton:
                dismiss();
                buttonClickListener.onStartLaterButtonPressed(this);
                break;
            case R.id.startNowButton:
                dismiss();
                buttonClickListener.onStartNowButtonPressed(this);
                break;
            default:
                break;
        }
    }

    public interface DialogButtonClickListener {
        void onCancelButtonPressed(Dialog dialog);

        void onStartLaterButtonPressed(Dialog dialog);

        void onStartNowButtonPressed(Dialog dialog);
    }
}
