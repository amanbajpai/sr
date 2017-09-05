package com.ros.smartrocket.ui.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WaitingUploadTaskBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.WaitingUploadTask;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.utils.helpers.APIFacade;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.UserActionsLogger;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskValidationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    @BindView(R.id.closingQuestionText)
    CustomTextView closingQuestionText;
    @BindView(R.id.missionDueTextView)
    CustomTextView missionDueTextView;
    @BindView(R.id.taskDataSizeTextView)
    CustomTextView taskDataSizeTextView;
    @BindView(R.id.dueInTextView)
    CustomTextView dueInTextView;
    @BindView(R.id.taskIdTextView)
    CustomTextView taskIdTextView;
    @BindView(R.id.sendNowButton)
    CustomButton sendNowButton;
    @BindView(R.id.sendLaterButton)
    CustomButton sendLaterButton;

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private Calendar calendar = Calendar.getInstance();


    private int taskId;
    private int missionId;
    private boolean firstlySelection;
    private boolean isRedo;
    private Task task = new Task();

    private AsyncQueryHandler handler;
    private List<NotUploadedFile> notUploadedFiles = new ArrayList<>();
    private List<Answer> answerListToSend = new ArrayList<>();
    private boolean hasFile = false;
    private float filesSizeB = 0;
    private View actionBarView;

    public TaskValidationActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_validation);
        ButterKnife.bind(this);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            firstlySelection = getIntent().getBooleanExtra(Keys.FIRSTLY_SELECTION, true);
            isRedo = getIntent().getBooleanExtra(Keys.IS_REDO, false);
        }

        handler = new DbHandler(getContentResolver());


        Button recheckAnswerButton = (Button) findViewById(R.id.recheckTaskButton);
        recheckAnswerButton.setOnClickListener(this);
        recheckAnswerButton.setVisibility(firstlySelection ? View.VISIBLE : View.GONE);

        sendNowButton.setOnClickListener(this);
        sendLaterButton.setOnClickListener(this);

        TasksBL.getTaskFromDBbyID(handler, taskId, missionId);
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    if (cursor != null && cursor.getCount() > 0) {
                        task = TasksBL.convertCursorToTask(cursor);

                        if (actionBarView != null) {
                            ((TextView) actionBarView.findViewById(R.id.titleTextView)).setText(task.getName());
                        }

                        answerListToSend = AnswersBL.getAnswersListToSend(task.getId(), task.getMissionId());
                        hasFile = AnswersBL.isHasFile(answerListToSend);
                        notUploadedFiles = AnswersBL.getTaskFilesListToUpload(task.getId(), task.getMissionId(), task.getName(), task.getLongEndDateTime());
                        filesSizeB = AnswersBL.getTaskFilesSizeMb(notUploadedFiles);

                        if (!isValidationLocationAdded(task) && isReadyToSend()) {
                            AnswersBL.saveValidationLocation(task, answerListToSend, hasFile);
                        }

                        setTaskData(task);
                        if (firstlySelection) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            QuestionsBL.getClosingStatementQuestionFromDB(handler, task.getWaveId(), task.getId(),
                                    task.getMissionId());
                        } else {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            sendNowButton.setBackgroundResource(R.drawable.button_blue_selector);
                            sendLaterButton.setBackgroundResource(R.drawable.button_blue_selector);

                            UIUtils.setActionBarBackground(TaskValidationActivity.this, task.getStatusId(), TasksBL.isPreClaimTask(task));
                            closingQuestionText.setText(R.string.task_has_not_yet_submitted2);
                        }
                    } else {
                        TasksBL.getTaskFromDBbyID(handler, task.getId(), task.getMissionId());
                    }
                    break;
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    List<Question> questions = QuestionsBL.convertCursorToQuestionList(cursor);
                    if (!questions.isEmpty()) {
                        Question question = questions.get(0);
                        String string = getString(R.string.task_has_not_yet_submitted, "\n" + question.getQuestion());
                        closingQuestionText.setMovementMethod(LinkMovementMethod.getInstance());
                        closingQuestionText.setText(Html.fromHtml(string));
                    } else {
                        closingQuestionText.setText(getString(R.string.task_has_not_yet_submitted, ""));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperationSuccess(BaseOperation operation) {
        hideLoading();
        if (Keys.START_TASK_OPERATION_TAG.equals(operation.getTag())) {
            task.setStartedStatusSent(true);
            TasksBL.updateTask(handler, task);
            sendTextAnswers();
        } else if (Keys.GET_NEW_TOKEN_OPERATION_TAG.equals(operation.getTag())) {
            sendNowButtonClick();
        } else if (Keys.SEND_ANSWERS_OPERATION_TAG.equals(operation.getTag())) {
            sendAnswerTextsSuccess();
        } else if (Keys.VALIDATE_TASK_OPERATION_TAG.equals(operation.getTag())) {
            task.setSubmittedAt(UIUtils.longToString(calendar.getTimeInMillis(), 2));
            task.setStatusId(Task.TaskStatusId.VALIDATION.getStatusId());
            TasksBL.updateTask(handler, task);
            QuestionsBL.removeQuestionsFromDB(this, task.getWaveId(), task.getId(), task.getMissionId());
            finishActivity();
        }
    }

    @Override
    public void onNetworkOperationFailed(BaseOperation operation) {
        hideLoading();
        if (Keys.SEND_ANSWERS_OPERATION_TAG.equals(operation.getTag())) {
            sendAnswerTextsSuccess();
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
            sendNowButton.setEnabled(true);
            sendLaterButton.setEnabled(true);
        }
    }

    public void setTaskData(Task task) {
        taskDataSizeTextView.setText(String.format(Locale.US, "%.1f", filesSizeB / 1024) + " " + getString(R.string
                .task_data_size_mb));
        taskIdTextView.setText(String.valueOf(task.getId()));
        long expireTimeInMillisecond = task.getLongExpireDateTime();
        if (expireTimeInMillisecond != 0) {
            long dueInMillisecond = expireTimeInMillisecond - calendar.getTimeInMillis();

            dueInTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, dueInMillisecond));
            missionDueTextView.setText(UIUtils.longToString(expireTimeInMillisecond, 3));
        } else {
            dueInTextView.setVisibility(View.INVISIBLE);
            missionDueTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void setFilesToUploadDbAndStartUpload(Boolean use3G) {
        //Add files data to DB and start upload
        WaitingUploadTaskBL.insertWaitingUploadTask(new WaitingUploadTask(task, notUploadedFiles.size()));
        for (NotUploadedFile notUploadedFile : notUploadedFiles) {
            notUploadedFile.setUse3G(use3G);
            notUploadedFile.setWaveId(task.getWaveId());
            notUploadedFile.setLatitudeToValidation(task.getLatitudeToValidation());
            notUploadedFile.setLongitudeToValidation(task.getLongitudeToValidation());

            FilesBL.insertNotUploadedFile(notUploadedFile);
        }
        startService(new Intent(TaskValidationActivity.this, UploadFileService.class).setAction(Keys
                .ACTION_CHECK_NOT_UPLOADED_FILES));
    }

    private void validateTask(final Task task) {
        showLoading(true);

        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(task.getLatitudeToValidation());
        location.setLongitude(task.getLongitudeToValidation());

        MatrixLocationManager.getAddressByLocation(location, (location1, countryName, cityName, districtName)
                -> sendNetworkOperation(apiFacade.getValidateTaskOperation(task.getWaveId(), task.getId(), task.getMissionId(), task.getLatitudeToValidation(), task.getLongitudeToValidation(), cityName)));
    }

    public void sendTextAnswers() {
        if ((UIUtils.is3G(this) && !preferencesManager.getUseOnlyWiFiConnaction()) || UIUtils.isWiFi(this)) {
            showLoading(false);
            apiFacade.sendAnswers(this, answerListToSend, missionId);
        } else {
            DialogUtils.showTurnOnWifiDialog(this);
        }
    }

    private void sendAnswerTextsSuccess() {
        if (!Config.USE_BAIDU) {
            AppEventsLogger logger = AppEventsLogger.newLogger(this);
            logger.logEvent(Keys.FB_LOGGING_SUBMITTED);
        }
        TasksBL.updateTaskStatusId(task.getId(), task.getMissionId(), Task.TaskStatusId.COMPLETED.getStatusId());

        if (hasFile) {
            if (UIUtils.is3G(this)
                    && (preferencesManager.get3GUploadTaskLimit() != 0 && filesSizeB / 1024 > preferencesManager.get3GUploadTaskLimit())
                    || (preferencesManager.get3GUploadMonthLimit() != 0 && preferencesManager.getUsed3GUploadMonthlySize
                    () + filesSizeB / 1024 > preferencesManager.get3GUploadMonthLimit())) {
                DialogUtils.show3GLimitExceededDialog(this, new DefaultInfoDialog.DialogButtonClickListener() {
                    @Override
                    public void onLeftButtonPressed(Dialog dialog) {
                        dialog.dismiss();
                        setFilesToUploadDbAndStartUpload(false);
                        finishActivity();
                    }

                    @Override
                    public void onRightButtonPressed(Dialog dialog) {
                        dialog.dismiss();
                        setFilesToUploadDbAndStartUpload(true);
                        finishActivity();
                    }
                });
            } else {
                setFilesToUploadDbAndStartUpload(true);
                finishActivity();
            }
        } else {
            validateTask(task);
        }
    }

    public boolean isReadyToSend() {
        return UIUtils.isOnline(this) && UIUtils.isAllLocationSourceEnabled(this)
                && preferencesManager.getUseLocationServices() && !UIUtils.isMockLocationEnabled(this, App.getInstance().getLocationManager().getLocation());
    }

    public void finishActivity() {
        if (firstlySelection) {
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + task.getWaveId()
                    + "_" + taskId + "_" + task.getMissionId());

            startActivity(IntentUtils.getMainActivityIntent(this));
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recheckTaskButton:
                TasksBL.updateTaskStatusId(taskId, task.getMissionId(),
                        isRedo ? Task.TaskStatusId.RE_DO_TASK.getStatusId() : Task.TaskStatusId.STARTED.getStatusId());

                Intent intent = isRedo ?
                        IntentUtils.getReCheckReDoQuestionsIntent(this, task.getId(), task.getMissionId())
                        : IntentUtils.getQuestionsIntent(this, task.getId(), task.getMissionId());
                startActivity(intent);
                finish();
                break;
            case R.id.sendNowButton:
                if (TextUtils.isEmpty(preferencesManager.getTokenForUploadFile()) ||
                        System.currentTimeMillis() - preferencesManager.getTokenUpdateDate() > DateUtils.HOUR_IN_MILLIS) {
                    apiFacade.getNewToken(this);
                } else {
                    sendNowButtonClick();
                }
                break;
            case R.id.sendLaterButton:
                sendLaterButtonClick();
                break;
            default:
                break;
        }
    }

    /**
     * Upload task right now.
     */
    public void sendNowButtonClick() {
        if (isReadyToSend()) {
            sendNowButton.setEnabled(false);
            sendLaterButton.setEnabled(false);
            if (!isValidationLocationAdded(task)) {
                if (hasFile) {
                    AnswersBL.savePhotoVideoAnswersAverageLocation(task, answerListToSend);

                    sendAnswers();
                } else {
                    MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
                        @Override
                        public void getLocationStart() {
                            showLoading(true);
                        }

                        @Override
                        public void getLocationInProcess() {
                        }

                        @Override
                        public void getLocationSuccess(Location location) {
                            if (!isFinishing()) {
                                saveLocationOfTaskToDb(task, location);

                                hideLoading();

                                sendAnswers();
                            } else {
                                saveLocationOfTaskToDb(task, location);
                            }
                        }

                        @Override
                        public void getLocationFail(String errorText) {
                            if (!isFinishing()) {
                                sendNowButton.setEnabled(true);
                                sendLaterButton.setEnabled(true);
                                UIUtils.showSimpleToast(TaskValidationActivity.this, errorText);
                            }
                        }
                    });
                }
            } else {
                sendAnswers();
            }
        } else {
            LocaleUtils.setCurrentLanguage();
            if (!UIUtils.isOnline(this)) {
                DialogUtils.showNetworkDialog(this);
            } else if (lm.getLocation() == null || !UIUtils.isAllLocationSourceEnabled(this)
                    || !UIUtils.isNetworkEnabled(this) || !preferencesManager.getUseLocationServices()) {
                DialogUtils.showLocationDialog(this, true);
            } else if (UIUtils.isMockLocationEnabled(this, lm.getLocation())) {
                DialogUtils.showMockLocationDialog(this, true);
            }
        }
    }

    /**
     * Postpone uploading and put it into local DB with flag.
     */
    public void sendLaterButtonClick() {
        UserActionsLogger.logTaskSubmitLater(task);
        if (!isValidationLocationAdded(task) && isReadyToSend()) {
            if (hasFile) {
                AnswersBL.savePhotoVideoAnswersAverageLocation(task, answerListToSend);

                finishActivity();
            } else {
                MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
                    @Override
                    public void getLocationStart() {
                        showLoading(true);
                    }

                    @Override
                    public void getLocationInProcess() {
                    }

                    @Override
                    public void getLocationSuccess(Location location) {
                        if (!isFinishing()) {
                            saveLocationOfTaskToDb(task, location);

                            hideLoading();

                            finishActivity();
                        } else {
                            saveLocationOfTaskToDb(task, location);
                        }
                    }

                    @Override
                    public void getLocationFail(String errorText) {
                        UIUtils.showSimpleToast(TaskValidationActivity.this, errorText);
                    }
                });
            }
        } else {
            finishActivity();
        }
    }

    public void sendAnswers() {
        if (task.getStartedStatusSent()) {
            sendTextAnswers();
        } else {
            showLoading(false);
            apiFacade.startTask(this, task);
        }
    }

    public boolean isValidationLocationAdded(Task task) {
        return task.getLatitudeToValidation() != 0 && task.getLongitudeToValidation() != 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!firstlySelection) {
                    finish();
                }
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!firstlySelection) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBarView = actionBar.getCustomView();

        if (task != null && !TextUtils.isEmpty(task.getName())) {
            ((TextView) actionBarView.findViewById(R.id.titleTextView)).setText(task.getName());
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    protected void onStop() {
        removeNetworkOperationListener(this);
        super.onStop();
    }

    private void saveLocationOfTaskToDb(Task task, Location location) {
        task.setLatitudeToValidation(location.getLatitude());
        task.setLongitudeToValidation(location.getLongitude());
        TasksBL.updateTask(task);
    }
}