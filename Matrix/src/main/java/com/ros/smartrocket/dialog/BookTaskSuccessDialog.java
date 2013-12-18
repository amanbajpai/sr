package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;
import com.ros.smartrocket.R;

public class BookTaskSuccessDialog extends Dialog implements View.OnClickListener {
    public static final String TAG = BookTaskSuccessDialog.class.getSimpleName();
    private Activity activity;

    public BookTaskSuccessDialog(Activity activity, String dateTime) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.dialog_book_task_success);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView) findViewById(R.id.title)).setText(activity.getString(R.string.book_task_success_dialog_title));
        ((TextView) findViewById(R.id.dateTime)).setText(dateTime);

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.startLaterButton).setOnClickListener(this);
        findViewById(R.id.startNowButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                break;
            case R.id.startLaterButton:
                dismiss();
                break;
            case R.id.startNowButton:
                dismiss();
                break;
            default:
                break;
        }
    }
}
