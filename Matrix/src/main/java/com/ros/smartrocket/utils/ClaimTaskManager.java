package com.ros.smartrocket.utils;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.location.Location;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.dialog.WithdrawTaskDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;

import java.util.Calendar;

import javax.annotation.Nonnull;

public class ClaimTaskManager implements NetworkOperationListenerInterface {
    private BaseActivity activity;
    private APIFacade apiFacade = APIFacade.getInstance();
    private AsyncQueryHandler handler;
    private ClaimTaskListener claimTaskListener;

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private Calendar calendar = Calendar.getInstance();

    private CustomProgressDialog progressDialog;

    private Task task;

    public ClaimTaskManager(@Nonnull BaseActivity activity, @Nonnull Task task, ClaimTaskListener claimTaskListener) {
        this.activity = activity;
        this.task = task;
        this.claimTaskListener = claimTaskListener;

        handler = new DbHandler(activity.getContentResolver());

        activity.addNetworkOperationListener(this);
    }

    public void claimTask() {
        showProgressBar();
        apiFacade.getQuestions(activity, task.getWaveId(), task.getId());
    }

    public void startTask() {
        if (UIUtils.isOnline(activity)) {
            showProgressBar();
            apiFacade.startTask(activity, task.getId());
        } else {
            changeStatusToStarted(false);
        }
    }

    public void unClaimTask() {
        String dateTime = UIUtils.longToString(task.getLongExpireDateTime(), 3);

        new WithdrawTaskDialog(activity, dateTime, new WithdrawTaskDialog.DialogButtonClickListener() {
            @Override
            public void onNoButtonPressed(Dialog dialog) {
            }

            @Override
            public void onYesButtonPressed(Dialog dialog) {
                showProgressBar();
                apiFacade.unclaimTask(activity, task.getId());
            }
        });
    }

    public void changeStatusToStarted(boolean startedStatusSent) {
        task.setStatusId(Task.TaskStatusId.STARTED.getStatusId());
        task.setStarted(UIUtils.longToString(calendar.getTimeInMillis(), 2));
        task.setStartedStatusSent(startedStatusSent);

        TasksBL.updateTask(handler, task);

        claimTaskListener.onStarted(task);
    }

    public void changeStatusToUnClaimed() {
        preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + task.getWaveId() + "_"
                + task.getId());

        task.setStatusId(Task.TaskStatusId.NONE.getStatusId());
        task.setStarted("");
        task.setIsMy(false);

        TasksBL.updateTask(handler, task);

        QuestionsBL.removeQuestionsFromDB(activity, task.getWaveId(), task.getId());
        AnswersBL.removeAnswersByTaskId(activity, task.getId());

        claimTaskListener.onUnClaimed(task);
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }


    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_QUESTIONS_OPERATION_TAG.equals(operation.getTag())) {

                MatrixLocationManager.getCurrentLocation(true, new MatrixLocationManager.GetCurrentLocationListener() {
                    @Override
                    public void getLocationStart() {
                    }

                    @Override
                    public void getLocationInProcess() {
                    }

                    @Override
                    public void getLocationSuccess(Location location) {
                        if (activity == null) {
                            return;
                        }
                        apiFacade.claimTask(activity, task.getId(), location.getLatitude(), location.getLongitude());
                    }
                });
            } else if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressBar();

                long startTimeInMillisecond = task.getLongStartDateTime();
                long preClaimedExpireInMillisecond = task.getLongPreClaimedTaskExpireAfterStart();
                long claimTimeInMillisecond = calendar.getTimeInMillis();
                long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();

                long missionDueMillisecond;
                if (TasksBL.isPreClaimTask(task)) {
                    missionDueMillisecond = startTimeInMillisecond + preClaimedExpireInMillisecond;
                } else {
                    missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
                }

                task.setStatusId(Task.TaskStatusId.CLAIMED.getStatusId());
                task.setIsMy(true);
                task.setClaimed(UIUtils.longToString(claimTimeInMillisecond, 2));
                task.setLongClaimDateTime(claimTimeInMillisecond);

                task.setExpireDateTime(UIUtils.longToString(missionDueMillisecond, 2));
                task.setLongExpireDateTime(missionDueMillisecond);

                TasksBL.updateTask(handler, task);

                String dateTime = UIUtils.longToString(missionDueMillisecond, 3);

                new BookTaskSuccessDialog(activity, task, dateTime, new BookTaskSuccessDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed(Dialog dialog) {
                        showProgressBar();
                        apiFacade.unclaimTask(activity, task.getId());
                    }

                    @Override
                    public void onStartLaterButtonPressed(Dialog dialog) {
                        claimTaskListener.onStartLater(task);
                    }

                    @Override
                    public void onStartNowButtonPressed(Dialog dialog) {
                        startTask();
                    }
                });

            } else if (Keys.UNCLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressBar();

                changeStatusToUnClaimed();

            } else if (Keys.START_TASK_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressBar();

                changeStatusToStarted(true);
            }
        } else {
            dismissProgressBar();

            if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag()) && operation.getResponseErrorCode() != null
                    && operation.getResponseErrorCode() == BaseNetworkService.MAXIMUM_MISSION_ERROR_CODE) {
                DialogUtils.showMaximumMissionDialog(activity);
            } else if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())
                    && operation.getResponseErrorCode() != null
                    && operation.getResponseErrorCode() == BaseNetworkService.MAXIMUM_CLAIM_PER_MISSION_ERROR_CODE) {
                UIUtils.showSimpleToast(activity, R.string.task_no_longer_available);
            } else {

                UIUtils.showSimpleToast(activity, operation.getResponseError());
            }
        }
    }

    public void showProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        progressDialog = CustomProgressDialog.show(activity);
        progressDialog.setCancelable(false);
    }

    public void dismissProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void onStop() {
        activity.addNetworkOperationListener(this);
    }

    public interface ClaimTaskListener {
        void onClaimed(Task task);

        void onUnClaimed(Task task);

        void onStartLater(Task task);

        void onStarted(Task task);
    }
}
