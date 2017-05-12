package com.ros.smartrocket.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.LocaleUtils;

public class DefaultInfoDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = DefaultInfoDialog.class.getSimpleName();
    private DialogButtonClickListener onDialogButtonClickListener;
    private TextView leftButton;
    private TextView rightButton;

    public DefaultInfoDialog(Context context, CharSequence title, CharSequence text, int leftButtonResId,
                             int rightButtonResId) {
        this(context, 0, R.drawable.info_icon, title, text, leftButtonResId, rightButtonResId);
    }

    public DefaultInfoDialog(Context context, int titleBackgroundColorResId, int titleIconResId, CharSequence title,
                             CharSequence text, int leftButtonResId, int rightButtonResId) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_default_info);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);

        if (titleBackgroundColorResId != 0) {
            titleTextView.setBackgroundColor(context.getResources().getColor(titleBackgroundColorResId));
        }

        if (titleIconResId != 0) {
            LocaleUtils.setCompoundDrawable(titleTextView, titleIconResId);
        }

        ((TextView) findViewById(R.id.text)).setText(text);

        leftButton = (TextView) findViewById(R.id.leftButton);
        rightButton = (TextView) findViewById(R.id.rightButton);

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
                if (onDialogButtonClickListener != null) {
                    onDialogButtonClickListener.onLeftButtonPressed(this);
                }
                break;
            case R.id.rightButton:
                if (onDialogButtonClickListener != null) {
                    onDialogButtonClickListener.onRightButtonPressed(this);
                }
                break;
            default:
                break;
        }
    }

    public void setOnDialogButtonClickListener(DialogButtonClickListener onDialogButtonClickListener) {

        this.onDialogButtonClickListener = onDialogButtonClickListener;
    }

    public interface DialogButtonClickListener {
        void onLeftButtonPressed(Dialog dialog);

        void onRightButtonPressed(Dialog dialog);
    }
}
