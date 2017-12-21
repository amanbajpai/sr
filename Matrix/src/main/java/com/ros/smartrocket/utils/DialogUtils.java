package com.ros.smartrocket.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.presentation.account.MyAccountMvpPresenter;
import com.ros.smartrocket.presentation.account.MyAccountMvpView;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.ui.dialog.QuiteTaskDialog;
import com.ros.smartrocket.ui.dialog.UpdateFirstLastNameDialog;
import com.ros.smartrocket.utils.helpers.WriteDataHelper;

import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;

/**
 * Utils class for easy work with UI Views
 */
public class DialogUtils {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int ONE_MB = 1024 * 1024;

    private DialogUtils() {
    }

    /**
     * Show location Dialog message
     *
     * @param activity - current activity
     */
    public static Dialog showLocationDialog(final Activity activity, final boolean isCancelable) {
        int cancelButtonResId = R.string.cancel;
        if (!isCancelable) {
            cancelButtonResId = R.string.logout;
        }

        DefaultInfoDialog locationDialog = new DefaultInfoDialog(activity, 0, R.drawable.info_icon,
                activity.getText(R.string.turn_on_location_dialog_title),
                activity.getText(R.string.turn_on_location_dialog_text),
                cancelButtonResId, R.string.settings);
        locationDialog.setCancelable(isCancelable);
        locationDialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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

                PreferencesManager preferencesManager = PreferencesManager.getInstance();
                if (!TextUtils.isEmpty(preferencesManager.getToken())
                        && !preferencesManager.getUseLocationServices()) {
                    activity.startActivity(IntentUtils.getSettingIntent(activity));
                } else {
                    activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }
        });

        return locationDialog;
    }

    /**
     * Show network Dialog message
     *
     * @param activity - current activity
     */
    public static Dialog showNetworkDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, 0, R.drawable.info_icon,
                activity.getText(R.string.turn_on_network_dialog_title),
                activity.getText(R.string.turn_on_network_dialog_text),
                R.string.cancel, R.string.settings);
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * @param activity - current activity
     */
    public static Dialog showGoogleSdkDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, 0, R.drawable.info_icon,
                activity.getText(R.string.turn_on_google_sdk_dialog_title),
                activity.getText(R.string.turn_on_google_sdk_dialog_text),
                R.string.cancel, R.string.settings);
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * @param activity - current activity
     */
    public static Dialog showMockLocationDialog(final Activity activity, final boolean isCancelable) {
        int cancelButtonResId = R.string.cancel;
        if (!isCancelable) {
            cancelButtonResId = R.string.logout;
        }

        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, 0, R.drawable.info_icon,
                activity.getText(R.string.turn_of_mock_location_dialog_title),
                activity.getText(R.string.turn_of_mock_location_dialog_text),
                cancelButtonResId, R.string.settings);
        dialog.setCancelable(isCancelable);
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
                    activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            }
        });
        return dialog;
    }

    /**
     * Show registration failed Dialog message
     *
     * @param context - current context
     */
    public static Dialog showLoginFailedDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context,
                context.getText(R.string.login_fail_dialog_title),
                context.getText(R.string.credentials_wrong),
                0, R.string.login_ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * @param activity - current activity
     */
    public static Dialog showAccountNotActivatedDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.login_fail_dialog_title),
                activity.getText(R.string.account_not_activated),
                R.string.request_new_activation_link, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                //TODO Request new activation link. Use Login, Pass
                //activity.startActivity(IntentUtils.getEmailIntent(null, null));
            }
        });
        return dialog;
    }

    /**
     * Show no internet connection Dialog message
     *
     * @param activity - current activity
     */
    public static Dialog showBadOrNoInternetDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.login_fail_dialog_title),
                activity.getText(R.string.internet_connection_is_bad),
                0, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * Show dialog. Photo can not be add.
     *
     * @param context - current context
     */
    public static Dialog showPhotoCanNotBeAddDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context,
                context.getText(R.string.add_photo_error_title),
                context.getText(R.string.add_photo_error_text),
                0, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * Show dialog. Photo is very big to upload.
     *
     * @param context - current context
     */
    public static Dialog showBigFileToUploadDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context,
                context.getText(R.string.big_file_error_title),
                context.getText(R.string.big_file_error_text),
                0, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * @param activity - current activity
     */
    public static Dialog show3GLimitExceededDialog(final Context activity,
                                                   DefaultInfoDialog.DialogButtonClickListener
                                                           dialogButtonClickListener) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.tree_g_limit_exceeded_dialog_title),
                activity.getText(R.string.tree_g_limit_exceeded_dialog_text1),
                R.string.tree_g_limit_exceeded_dialog_only_wifi, R.string.tree_g_limit_exceeded_dialog_yes);
        dialog.setCancelable(false);
        dialog.setOnDialogButtonClickListener(dialogButtonClickListener);

        return dialog;
    }

    /**
     * Show quite task Dialog message
     *
     * @param activity - current activity
     */
    public static Dialog showQuiteTaskDialog(final Activity activity, final int waveId, final int taskId,
                                             final int missionId) {
        QuiteTaskDialog dialog = new QuiteTaskDialog(activity);
        dialog.setOnDialogButtonClickListener(new QuiteTaskDialog.DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onQuiteTaskButtonPressed(Dialog dialog) {
                PreferencesManager preferencesManager = PreferencesManager.getInstance();

                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + waveId + "_" + taskId +
                        "_" + missionId);

                AnswersBL.clearTaskUserAnswers(activity, taskId);
                QuestionsBL.recoverQuestionTable(activity, waveId, taskId, missionId);
                dialog.dismiss();
                activity.finish();
            }
        });
        return dialog;
    }

    /**
     * Show account not activated Dialog message
     *
     * @param activity - current activity
     */
    public static Dialog showMaximumMissionDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, R.color.green, R.drawable.info_icon,
                activity.getText(R.string.maximum_mission_dialog_title),
                activity.getText(R.string.maximum_mission_dialog_text),
                0, android.R.string.ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * Age verification Dialog message
     *
     * @param context - current context
     */
    public static Dialog showAgeVerificationDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context, 0, R.drawable.plus_18,
                context.getText(R.string.age_verification_dialog_title),
                context.getText(R.string.age_verification_dialog_text1),
                R.string.age_verification_dialog_under, R.string.age_verification_dialog_redo);
        dialog.setCancelable(false);
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
                context.startActivity(IntentUtils.getLoginIntentForLogout(context));
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
            }
        });

        return dialog;
    }

    /**
     * Account confirmed Dialog
     *
     * @param activity - current activity
     */
    public static Dialog showAccountConfirmedDialog(final Activity activity) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity, R.color.green, R.drawable.confirm_icon,
                activity.getText(R.string.account_confirmed_dialog_title),
                activity.getText(R.string.account_confirmed_dialog_text1),
                0, R.string.ok);
        dialog.setCancelable(false);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                activity.finish();
                activity.startActivity(IntentUtils.getLoginIntentForLogout(activity));
            }
        });

        return dialog;
    }

    /**
     * User already exist Dialog
     *
     * @param context - current context
     */
    public static Dialog showUserAlreadyExistDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context, R.color.red, R.drawable.info_icon,
                context.getText(R.string.user_already_exists_dialog_title),
                context.getText(R.string.user_already_exists_dialog_text1),
                0, R.string.user_already_exists_dialog_ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * User already exist Dialog
     *
     * @param context - current context
     */
    public static Dialog showNotAllFilesSendDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context, R.color.red, R.drawable.info_icon,
                context.getText(R.string.not_all_files_sent_dialog_title),
                context.getText(R.string.not_all_files_sent_dialog_text),
                0, R.string.user_already_exists_dialog_ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * Turn on Wi-Fi or change settings dialog
     *
     * @param context - current context
     */
    public static Dialog showTurnOnWifiDialog(final Context context) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context, R.color.red, R.drawable.info_icon,
                context.getText(R.string.turn_on_wifi_dialog_title),
                context.getText(R.string.turn_on_wifi_dialog_text1),
                0, R.string.turn_on_wifi_dialog_ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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
     * Check download media
     *
     * @param context - current context
     */
    public static Dialog showDownloadMediaDialog(final Context context, int missionSize,
                                                 DefaultInfoDialog.DialogButtonClickListener listener) {
        String size = String.format("%.0f", Math.ceil((double) missionSize / ONE_MB));
        DefaultInfoDialog dialog = new DefaultInfoDialog(context, R.color.red, R.drawable.info_icon,
                context.getText(R.string.turn_on_wifi_dialog_title),
                context.getString(R.string.mission_size_dialog_text, size),
                R.string.continue_register, R.string.cancel_big);
        dialog.setOnDialogButtonClickListener(listener);

        return dialog;
    }

    /**
     * Ask user to confirm changing first and last name
     *
     * @param context - current context
     */
    public static Dialog showAreYouSureUserNameDialog(final Context context, final String name,
                                                      final UpdateFirstLastNameDialog.DialogButtonClickListener
                                                              listener) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context, R.color.red, R.drawable.info_icon,
                context.getText(R.string.dialog_attention),
                context.getString(R.string.dialog_are_you_sure_first_last_name_text, name),
                R.string.cancel, R.string.confirm);
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
                listener.onCancelButtonPressed();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                listener.onUpdateButtonPressed(name);
            }
        });

        return dialog;
    }

    /// ======================================================================================================== ///
    /// ============================================== ID CARD ================================================= ///
    /// ======================================================================================================== ///

    public static void showUpdateFirstLastNameDialog(Activity activity, MyAccountMvpPresenter<MyAccountMvpView> presenter) {
        Dialog dialog = new UpdateFirstLastNameDialog(activity,
                new UpdateFirstLastNameDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed() {
                        // nothing
                    }

                    @Override
                    public void onUpdateButtonPressed(String name) {
                        showAreYouSureDialog(activity, name, presenter);
                    }
                });
        dialog.show();
    }

    private static void showAreYouSureDialog(final Activity activity, String name, MyAccountMvpPresenter<MyAccountMvpView> presenter) {
        DialogUtils.showAreYouSureUserNameDialog(activity, name,
                new UpdateFirstLastNameDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed() {
                        // nothing
                    }

                    @Override
                    public void onUpdateButtonPressed(String name) {
                        presenter.updateUserName(name);
                    }
                });
    }

    public static Dialog showWhyWeNeedThisDialog(final Context context, int titleRes, int textRes) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(context, R.color.red, R.drawable.info_icon,
                context.getText(titleRes),
                context.getText(textRes),
                0, R.string.user_already_exists_dialog_ok);
        dialog.hideLeftButton();
        dialog.setOnDialogButtonClickListener(new DefaultInfoDialog.DialogButtonClickListener() {
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

    public static Dialog showEndAudioRecordingDialog(final Context activity,
                                                     DefaultInfoDialog.DialogButtonClickListener
                                                             dialogButtonClickListener) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.recording),
                activity.getText(R.string.recording_end),
                R.string.no_button, R.string.turn_on_wifi_dialog_ok);
        dialog.setCancelable(false);
        dialog.setOnDialogButtonClickListener(dialogButtonClickListener);
        return dialog;
    }

    public static Dialog showDeleteAudioRecordingDialog(final Context activity,
                                                        DefaultInfoDialog.DialogButtonClickListener
                                                                dialogButtonClickListener) {
        DefaultInfoDialog dialog = new DefaultInfoDialog(activity,
                activity.getText(R.string.recording),
                activity.getText(R.string.recording_delete),
                R.string.no_button, R.string.turn_on_wifi_dialog_ok);
        dialog.setCancelable(false);
        dialog.setOnDialogButtonClickListener(dialogButtonClickListener);
        return dialog;
    }
}
