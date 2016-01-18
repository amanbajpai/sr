package com.ros.smartrocket.utils;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.location.Location;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.activity.BaseActivity;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.entity.ClaimTaskResponse;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.dialog.ShowProgressDialogInterface;
import com.ros.smartrocket.dialog.WithdrawTaskDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class ClaimTaskManager implements NetworkOperationListenerInterface, ShowProgressDialogInterface {
    private BaseActivity activity;
    private APIFacade apiFacade = APIFacade.getInstance();
    private AsyncQueryHandler handler;
    private ClaimTaskListener claimTaskListener;
    private Location location;

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private FileProcessingManager fileProcessingManager = FileProcessingManager.getInstance();
    private Calendar calendar = Calendar.getInstance();

    private CustomProgressDialog progressDialog;

    private Task task;
    private String claimDialogDateTime;

    public ClaimTaskManager(@NonNull BaseActivity activity, @NonNull Task task, ClaimTaskListener claimTaskListener) {
        this.activity = activity;
        this.task = task;
        this.claimTaskListener = claimTaskListener;

        handler = new DbHandler(activity.getContentResolver());

        activity.addNetworkOperationListener(this);
    }

    /// ======================================================================================================== ///
    /// ===================================== DB AND NETWORK CALLBACKS ========================================= ///
    /// ======================================================================================================== ///

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    List<Question> questions = QuestionsBL.convertCursorToQuestionList(cursor);
                    List<Question> instructionQuestions = QuestionsBL.getInstructionQuestionList(questions);

                    if (!instructionQuestions.isEmpty()) {
                        downloadInstructionQuestionFile(0, instructionQuestions);
                    } else {
                        apiFacade.getQuestions(activity, task.getWaveId(), task.getId(), task.getMissionId());
                    }

                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.GET_QUESTIONS_OPERATION_TAG.equals(operation.getTag())) {

                MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
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

                        ClaimTaskManager.this.location = location;

                        Wave wave = WavesBL.convertCursorToWave(WavesBL.getWaveFromDBbyID(task.getWaveId()));
                        if (wave.getDownloadMediaWhenClaimingTask()) {
                            QuestionsBL.getQuestionsListFromDB(
                                    handler, task.getWaveId(), task.getId(), task.getMissionId(), true);
                        } else {
                            apiFacade.claimTask(activity, task.getId(),
                                    location.getLatitude(), location.getLongitude());
                        }
                    }

                    @Override
                    public void getLocationFail(String errorText) {
                        UIUtils.showSimpleToast(App.getInstance(), errorText);
                    }
                });
            } else if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressBar();
                ClaimTaskResponse claimTaskResponse = (ClaimTaskResponse) operation.getResponseEntities().get(0);

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

                task.setMissionId(claimTaskResponse.getMissionId());
                task.setStatusId(Task.TaskStatusId.CLAIMED.getStatusId());
                task.setIsMy(true);
                task.setClaimed(UIUtils.longToString(claimTimeInMillisecond, 2));
                task.setLongClaimDateTime(claimTimeInMillisecond);

                task.setExpireDateTime(UIUtils.longToString(missionDueMillisecond, 2));
                task.setLongExpireDateTime(missionDueMillisecond);

                TasksBL.updateTask(handler, task, null);

                QuestionsBL.setMissionId(task.getWaveId(), task.getId(), task.getMissionId());
                AnswersBL.setMissionId(task.getId(), task.getMissionId());

                claimDialogDateTime = UIUtils.longToString(missionDueMillisecond, 3);

                if (claimTaskResponse.isUpdateRequired()) {
                    DialogUtils.showIdCardIsSupportedDialog(activity, apiFacade, this);
                } else {
                    showClaimDialog(claimDialogDateTime);
                }

            } else if (Keys.UNCLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressBar();

                changeStatusToUnClaimed();

            } else if (Keys.START_TASK_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressBar();

                changeStatusToStarted(true);
            } else if (Keys.UPDATE_USER_OPERATION_TAG.equals(operation.getTag())) {
                dismissProgressBar();
                showClaimDialog(claimDialogDateTime);
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

    /// ======================================================================================================== ///
    /// =============================================== DIALOGS ================================================ ///
    /// ======================================================================================================== ///

    private void showClaimDialog(String dateTime) {
        new BookTaskSuccessDialog(activity, task, dateTime, new BookTaskSuccessDialog
                .DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed(Dialog dialog) {
                showDialog();
                apiFacade.unclaimTask(activity, task.getId(), task.getMissionId());
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
    }

    public void onStop() {
        activity.addNetworkOperationListener(this);
    }

    public void claimTask() {
        showDialog();
        apiFacade.getQuestions(activity, task.getWaveId(), task.getId(), task.getMissionId());
    }

    public void startTask() {
        if (UIUtils.isOnline(activity)) {
            showDialog();
            apiFacade.startTask(activity, task.getWaveId(), task.getId(), task.getMissionId());
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
                showDialog();
                apiFacade.unclaimTask(activity, task.getId(), task.getMissionId());
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
                + task.getId() + "_" + task.getMissionId());

        Integer missionId = task.getMissionId();
        task.setStatusId(Task.TaskStatusId.NONE.getStatusId());
        task.setStarted("");
        task.setIsMy(false);
        task.setMissionId(null);

        TasksBL.updateTask(handler, task, missionId);

        QuestionsBL.removeQuestionsFromDB(activity, task.getWaveId(), task.getId(), task.getMissionId());
        AnswersBL.removeAnswersByTaskId(activity, task.getId());

        claimTaskListener.onUnClaimed(task);
    }

    public void downloadInstructionQuestionFile(final int startFrom, final List<Question> questions) {
        final Question question = questions.get(startFrom);

        String fileUrl = "";
        FileProcessingManager.FileType fileType = FileProcessingManager.FileType.IMAGE;
        if (!TextUtils.isEmpty(question.getPhotoUrl())) {
            fileUrl = question.getPhotoUrl();
        } else if (!TextUtils.isEmpty(question.getVideoUrl())) {
            fileUrl = question.getVideoUrl();
            fileType = FileProcessingManager.FileType.VIDEO;
        }

        fileProcessingManager.getFileByUrl(fileUrl, fileType, new FileProcessingManager.OnLoadFileListener() {
            @Override
            public void onStartFileLoading() {

            }

            @Override
            public void onFileLoaded(File file) {
                QuestionsBL.updateInstructionFileUri(question.getWaveId(),
                        question.getTaskId(), question.getMissionId(), question.getId(),
                        file.getPath());

                if (questions.size() == startFrom + 1) {
                    apiFacade.claimTask(activity, task.getId(), location.getLatitude(), location.getLongitude());
                } else {
                    downloadInstructionQuestionFile(startFrom + 1, questions);
                }
            }

            @Override
            public void onFileLoadingError() {
                UIUtils.showSimpleToast(activity, R.string.internet_connection_is_bad);
                dismissProgressBar();
            }
        });
    }

    @Override
    public void showDialog() {
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

    public interface ClaimTaskListener {
        void onClaimed(Task task);

        void onUnClaimed(Task task);

        void onStartLater(Task task);

        void onStarted(Task task);
    }
}
