package com.ros.smartrocket.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;


public class CheckLocationDialog extends Dialog {
    private static final String TAG = CheckLocationDialog.class.getSimpleName();
    private static final int DELAY_MILLIS = 1000;
    private Activity activity;
    private ImageView statusImage;
    private TextView statusText;

    public CheckLocationDialog(final Activity activity) {
        super(activity);
        this.activity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            show();
        } catch (Exception e) {
            L.e(TAG, "Show dialog error" + e.getMessage(), e);
        }
        setContentView(R.layout.dialog_check_location_success);
        setCancelable(true);

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        statusImage = (ImageView) findViewById(R.id.statusImage);
        statusImage.setImageResource(R.drawable.round_progress);
        statusImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate));

        statusText = (TextView) findViewById(R.id.statusText);
        statusText.setText(activity.getString(R.string.check_location_dialog_text1));

    }


    public void checkLocationSuccess() {
        statusImage.clearAnimation();
        statusImage.setImageResource(R.drawable.ok_progress);
        statusText.setText(activity.getString(R.string.check_location_dialog_text2));
        hideDialog();
    }

    public void checkLocationFail() {
        statusImage.clearAnimation();
        statusImage.setImageResource(R.drawable.error_progress);
        statusText.setText(activity.getString(R.string.check_location_dialog_text3));
        hideDialog();
    }

    private void hideDialog() {
        new Handler().postDelayed(this::dismiss, DELAY_MILLIS);
    }
}
