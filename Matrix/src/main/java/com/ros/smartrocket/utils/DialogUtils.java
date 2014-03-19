package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.DefaultInfoDialog;
import com.ros.smartrocket.dialog.QuiteTaskDialog;
import com.ros.smartrocket.helpers.WriteDataHelper;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

/**
 * Utils class for easy work with UI Views
 */
public class DialogUtils {
    //private static final String TAG = "DialogUtils";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Show location Dialog message
     *
     * @param activity
     */
    public static Dialog showLocationDialog(final Activity activity, final boolean isCancelable) {
        int cancelButtonResId = R.string.cancel;
        if (!isCancelable) {
            cancelButtonResId = R.string.logout;
        }

        DefaultInfoDialog locationDialog = new DefaultInfoDialog(activity, R.drawable.info_icon,
                activity.getText(R.string.turn_on_location_dialog_title),
                activity.getText(R.string.turn_on_location_dialog_text),
                cancelButtonResId, R.string.settings);
        locationDialog.setCancelable(isCancelable);
        locationDialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                if (isCancelable) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    WriteDataHelper.prepareLogout(activity);

                    activity.startActivity(IntentUtils.getLoginIntentForLogout(activity));
                    activity.finish();
                }
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        return locationDialog;
    }

    /**
     * Show network Dialog message
     *
     * @param activity
     */
    public static Dialog showNetworkDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, R.drawable.info_icon,
                activity.getText(R.string.turn_on_network_dialog_title),
                activity.getText(R.string.turn_on_network_dialog_text),
                R.string.cancel, R.string.settings);
        dialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
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
        return dialog;
    }

    /**
     * Show Google SDK Dialog message
     *
     * @param activity
     */
    public static Dialog showGoogleSdkDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, R.drawable.info_icon,
                activity.getText(R.string.turn_on_google_sdk_dialog_title),
                activity.getText(R.string.turn_on_google_sdk_dialog_text),
                R.string.cancel, R.string.settings);
        dialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
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

        return dialog;
    }

    /**
     * Show mock location Dialog message
     *
     * @param activity
     */
    public static Dialog showMockLocationDialog(final Activity activity, final boolean isCancelable) {
        int cancelButtonResId = R.string.cancel;
        if (!isCancelable) {
            cancelButtonResId = R.string.logout;
        }

        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, R.drawable.info_icon,
                activity.getText(R.string.turn_of_mock_location_dialog_title),
                activity.getText(R.string.turn_of_mock_location_dialog_text),
                cancelButtonResId, R.string.settings);
        dialog.setCancelable(isCancelable);
        dialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                if (isCancelable) {
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    WriteDataHelper.prepareLogout(activity);

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
        return dialog;
    }

    /**
     * Show registration failed Dialog message
     *
     * @param context
     */
    public static Dialog showLoginFailedDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context,
                context.getText(R.string.login_fail_dialog_title),
                context.getText(R.string.credentials_wrong),
                0, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * Show account not activated Dialog message
     *
     * @param activity
     */
    public static Dialog showAccountNotActivatedDialog(final Activity activity) {
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
        return dialog;
    }

    /**
     * Show dialog. Photo can not be add.
     *
     * @param context
     */
    public static Dialog showPhotoCanNotBeAddDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context,
                context.getText(R.string.add_photo_error_title),
                context.getText(R.string.add_photo_error_text),
                0, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClicklistener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * Show 3G limit reached Dialog message
     *
     * @param activity
     */
    public static Dialog show3GLimitExceededDialog(final Context activity,
                                                   DefaultInfoDialog.DialogButtonClickListener
                                                           dialogButtonClickListener) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.tree_g_limit_exceeded_dialog_title),
                activity.getText(R.string.tree_g_limit_exceeded_dialog_text1),
                R.string.tree_g_limit_exceeded_dialog_only_wifi, R.string.tree_g_limit_exceeded_dialog_yes);
        dialog.setCancelable(false);
        dialog.setOnDialogButtonClicklistener(dialogButtonClickListener);

        return dialog;
    }

    /**
     * Show quite task Dialog message
     *
     * @param activity
     */
    public static Dialog showQuiteTaskDialog(final Activity activity, final int surveyId, final int taskId) {
        QuiteTaskDialog dialog = new QuiteTaskDialog(activity);
        dialog.setOnDialogButtonClicklistener(new QuiteTaskDialog.DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onQuiteTaskButtonPressed(Dialog dialog) {
                PreferencesManager preferencesManager = PreferencesManager.getInstance();

                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + surveyId + "_" + taskId);

                AnswersBL.clearTaskUserAnswers(activity, taskId);
                dialog.dismiss();
                activity.finish();
            }
        });
        return dialog;
    }

    /**
     * Show quite task Dialog message
     *
     * @param activity
     */
    public static Dialog showReCheckAnswerTaskDialog(final Activity activity, final int surveyId, final int taskId) {
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
                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + surveyId + "_" + taskId);

                AnswersBL.clearTaskUserAnswers(activity, taskId);
                dialog.dismiss();

                activity.startActivity(IntentUtils.getQuestionsIntent(activity, surveyId, taskId));
                activity.finish();
            }
        });
        return dialog;
    }
}
