package com.matrix.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.matrix.R;

public class DefaultInfoDialog extends Dialog implements View.OnClickListener {
    public static final String TAG = DefaultInfoDialog.class.getSimpleName();
    private DialogButtonClickListener onDialogButtonClicklistener;

    public DefaultInfoDialog(Activity activity, CharSequence title, CharSequence text, int leftButtonResId, int rightButtonResId) {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setContentView(R.layout.dialog_default_info);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.text)).setText(text);

        Button leftButton = (Button) findViewById(R.id.leftButton);
        Button rightButton = (Button) findViewById(R.id.rightButton);

        leftButton.setText(leftButtonResId);
        rightButton.setText(rightButtonResId);

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftButton:
                if (onDialogButtonClicklistener != null) {
                    onDialogButtonClicklistener.onLeftButtonPressed(this);
                }
                break;
            case R.id.rightButton:
                if (onDialogButtonClicklistener != null) {
                    onDialogButtonClicklistener.onRightButtonPressed(this);
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
        public void onLeftButtonPressed(Dialog dialog);

        public void onRightButtonPressed(Dialog dialog);
    }
}