package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.provider.Settings;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.DefaultInfoDialog;
import com.ros.smartrocket.dialog.QuiteTaskDialog;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

/**
 * Utils class for easy work with UI Views
 */
public class DialogUtils {
    //private static final String TAG = "UIUtils";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Show location Dialog message
     *
     * @param activity
     */
    public static void showLocationDialog(final Activity activity) {
        DefaultInfoDialog locationDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.turn_on_location_dialog_title),
                activity.getText(R.string.turn_on_location_dialog_text),
                R.string.cancel, R.string.settings);
        locationDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
    }

    /**
     * Show network Dialog message
     *
     * @param activity
     */
    public static void showNetworkDialog(final Activity activity) {
        DefaultInfoDialog networkDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.turn_on_network_dialog_title),
                activity.getText(R.string.turn_on_network_dialog_text),
                R.string.cancel, R.string.settings);
        networkDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
    }

    /**
     * Show Google SDK Dialog message
     *
     * @param activity
     */
    public static void showGoogleSdkDialog(final Activity activity) {
        DefaultInfoDialog networkDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.turn_on_google_sdk_dialog_title),
                activity.getText(R.string.turn_on_google_sdk_dialog_text),
                R.string.cancel, R.string.settings);
        networkDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                int resultCode = isGooglePlayServicesAvailable(activity);
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
        });
    }

    /**
     * Show mock location Dialog message
     *
     * @param activity
     */
    public static void showMockLocationDialog(final Activity activity, final boolean isCancelable) {
        int cancelButtonResId = R.string.cancel;
        if (!isCancelable) {
            cancelButtonResId = R.string.logout;
        }

        DefaultInfoDialog networkDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.turn_of_mock_location_dialog_title),
                activity.getText(R.string.turn_of_mock_location_dialog_text),
                cancelButtonResId, R.string.settings);
        networkDialog.setCancelable(isCancelable);
        networkDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                if (isCancelable) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    PreferencesManager.getInstance().setToken("");

                    activity.startActivity(IntentUtils.getLoginIntentForLogout(activity));
                    activity.finish();
                }
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                if (UIUtils.isIntentAvailable(activity, intent)) {
                    activity.startActivity(intent);
                } else {
                    activity.startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                }
            }
        });
    }

    /**
     * Show registration failed Dialog message
     *
     * @param activity
     */
    public static void showRegistrationFailedDialog(final Activity activity) {
        DefaultInfoDialog networkDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.login_fail_dialog_title),
                activity.getText(R.string.credentials_wrong),
                0, android.R.string.ok);
        networkDialog.hideLeftButton();
        networkDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Show account not activated Dialog message
     *
     * @param activity
     */
    public static void showAccountNotActivatedDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.login_fail_dialog_title),
                activity.getText(R.string.account_not_activated),
                0, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                //activity.startActivity(IntentUtils.getEmailIntent(null, null));
            }
        });
    }

    /**
     * Show 3G limit reached Dialog message
     *
     * @param activity
     */
    public static void show3GLimitExceededDialog(final Activity activity, DefaultInfoDialog.DialogButtonClickListener dialogButtonClickListener) {
        DefaultInfoDialog networkDialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.tree_g_limit_exceeded_dialog_title),
                activity.getText(R.string.tree_g_limit_exceeded_dialog_text1),
                R.string.tree_g_limit_exceeded_dialog_only_wifi, R.string.tree_g_limit_exceeded_dialog_yes);
        networkDialog.setOnDialogButtonClicklistener(dialogButtonClickListener);
    }

    /**
     * Show quite task Dialog message
     *
     * @param activity
     */
    public static void showQuiteTaskDialog(final Activity activity, final int taskId) {
        QuiteTaskDialog dialog = new QuiteTaskDialog(activity);
        dialog.setOnDialogButtonClicklistener(new QuiteTaskDialog.DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onQuiteTaskButtonPressed(Dialog dialog) {
                PreferencesManager preferencesManager = PreferencesManager.getInstance();

                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + taskId);

                AnswersBL.clearTaskUserAnswers(activity, taskId);
                dialog.dismiss();
                activity.finish();
            }
        });
    }

    /**
     * Show quite task Dialog message
     *
     * @param activity
     */
    public static void showReCheckAnswerTaskDialog(final Activity activity, final int surveyId, final int taskId) {
        QuiteTaskDialog dialog = new QuiteTaskDialog(activity);
        dialog.setOnDialogButtonClicklistener(new QuiteTaskDialog.DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onQuiteTaskButtonPressed(Dialog dialog) {
                TasksBL.updateTaskStatusId(taskId, Task.TaskStatusId.started.getStatusId());

                PreferencesManager preferencesManager = PreferencesManager.getInstance();
                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + taskId);

                AnswersBL.clearTaskUserAnswers(activity, taskId);
                dialog.dismiss();

                activity.startActivity(IntentUtils.getQuestionsIntent(activity, surveyId, taskId));
                activity.finish();
            }
        });
    }
}
