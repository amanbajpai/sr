package com.matrix.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.matrix.R;
import com.matrix.dialog.DefaultInfoDialog;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

/**
 * Utils class for easy work with UI Views
 */
public class DialogUtils {
    private static final String TAG = "UIUtils";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Show simple Toast message
     *
     * @param activity
     */
    public static void showLocationDialog(final Activity activity) {
        DefaultInfoDialog locationDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.turn_on_location_dialog_title),
                activity.getText(R.string.turn_on_location_dialog_text),
                R.string.settings, R.string.cancel);
        locationDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Show simple Toast message
     *
     * @param activity
     */
    public static void showNetworkDialog(final Activity activity) {
        DefaultInfoDialog networkDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.turn_on_network_dialog_title),
                activity.getText(R.string.turn_on_network_dialog_text),
                R.string.settings, R.string.cancel);
        networkDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Show simple Toast message
     *
     * @param activity
     */
    public static void showGoogleSdkDialog(final Activity activity) {
        DefaultInfoDialog networkDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.turn_on_google_sdk_dialog_title),
                activity.getText(R.string.turn_on_google_sdk_dialog_text),
                R.string.settings, R.string.cancel);
        networkDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                int resultCode = isGooglePlayServicesAvailable(activity);
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }
}