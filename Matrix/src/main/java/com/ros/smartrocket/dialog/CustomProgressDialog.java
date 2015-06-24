package com.ros.smartrocket.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.utils.L;

/**
 * Show custom dialog with progress bar
 */

public class CustomProgressDialog extends Dialog {
    private static final String TAG = CustomProgressDialog.class.getSimpleName();
    private static LayoutInflater inflater;

    public CustomProgressDialog(Context context) {
        super(context, R.style.customProgressDialog);
    }

    public static CustomProgressDialog show(Activity activity, CharSequence title, CharSequence message) {
        if (activity != null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.progres_dialog_with_text, null);

            ImageView loadingImage = (ImageView) view.findViewById(R.id.loading_image);
            loadingImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate));

            TextView statusText = (TextView) view.findViewById(R.id.status_text);
            statusText.setText(message);

            CustomProgressDialog dialog = new CustomProgressDialog(activity);
            dialog.setTitle(title);
            dialog.setCancelable(false);
            dialog.addContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            try {
                if (!activity.isFinishing()) {
                    dialog.show();
                }
                return dialog;
            } catch (Exception e) {
                L.e(TAG, "Show dialog error" + e.getMessage(), e);
            }
        }
        return null;
    }

    public static CustomProgressDialog show(Activity activity) {
        if (activity != null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.progres_dialog, null);

            ImageView loadingImage = (ImageView) view.findViewById(R.id.loading_image);
            loadingImage.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.rotate));

            CustomProgressDialog dialog = new CustomProgressDialog(activity);
            dialog.setCancelable(true);
            dialog.addContentView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            try {
                if (!activity.isFinishing()) {
                    dialog.show();
                }
                return dialog;
            } catch (Exception e) {
                L.e(TAG, "Error show dialog: " + e.getMessage(), e);
            }
        }
        return null;
    }

    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            L.e(TAG, "Error dismiss dialog: " + e.getMessage(), e);
        }
    }
}
