package com.ros.smartrocket.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.ros.smartrocket.R;

public class DefaultInfoDialog extends Dialog implements View.OnClickListener {
    //private static final String TAG = DefaultInfoDialog.class.getSimpleName();
    private DialogButtonClickListener onDialogButtonClicklistener;
    private Button leftButton;
    private Button rightButton;

    public DefaultInfoDialog(Context context, CharSequence title, CharSequence text, int leftButtonResId,
                             int rightButtonResId) {
        super(context);
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

        leftButton = (Button) findViewById(R.id.leftButton);
        rightButton = (Button) findViewById(R.id.rightButton);

        if (leftButtonResId != 0) {
            leftButton.setText(leftButtonResId);
        }
        if (rightButtonResId != 0) {
            rightButton.setText(rightButtonResId);
        }

        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
    }

    public void hideLeftButton() {
        leftButton.setVisibility(View.GONE);
    }

    public void hideRightButton() {
        rightButton.setVisibility(View.GONE);
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
        void onLeftButtonPressed(Dialog dialog);

        void onRightButtonPressed(Dialog dialog);
    }
}
