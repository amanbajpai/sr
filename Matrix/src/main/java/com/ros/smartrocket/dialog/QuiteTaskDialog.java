package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import com.ros.smartrocket.R;

public class QuiteTaskDialog extends Dialog implements View.OnClickListener {
    //private static final String TAG = QuiteTaskDialog.class.getSimpleName();
    //private Activity activity;
    private DialogButtonClickListener onDialogButtonClicklistener;

    public QuiteTaskDialog(Activity activity) {
        super(activity);
        //this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.dialog_quite_task);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        findViewById(R.id.cancelButton).setOnClickListener(this);
        findViewById(R.id.quiteTaskButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                if (onDialogButtonClicklistener != null) {
                    onDialogButtonClicklistener.onCancelButtonPressed(this);
                }
                break;
            case R.id.quiteTaskButton:
                if (onDialogButtonClicklistener != null) {
                    onDialogButtonClicklistener.onQuiteTaskButtonPressed(this);
                }
                break;
            default:
                break;
        }
    }

    public void setOnDialogButtonClicklistener(DialogButtonClickListener onDialogButtonClicklistener) {

        this.onDialogButtonClicklistener = onDialogButtonClicklistener;
    }

    public interface DialogButtonClickListener {
        void onCancelButtonPressed(Dialog dialog);

        void onQuiteTaskButtonPressed(Dialog dialog);
    }
}
