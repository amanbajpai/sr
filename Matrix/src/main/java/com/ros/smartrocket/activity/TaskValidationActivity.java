package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.FilesBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.QuestionDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.NotUploadedFile;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.DefaultInfoDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskValidationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private Calendar calendar = Calendar.getInstance();
    private TextView missionDueTextView;
    private TextView taskDataSizeTextView;
    private TextView dueInTextView;
    private TextView closingQuestionText;

    private int taskId;
    private int missionId;
    private boolean firstlySelection;
    private boolean isRedo;
    private Task task = new Task();

    private AsyncQueryHandler handler;
    private List<NotUploadedFile> notUploadedFiles = new ArrayList<NotUploadedFile>();
    private List<Answer> answerListToSend = new ArrayList<Answer>();
    private boolean hasFile = false;
    private float filesSizeB = 0;

    private Button sendNowButton;
    private Button sendLaterButton;

    private View actionBarView;

    public TaskValidationActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_task_validation);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            firstlySelection = getIntent().getBooleanExtra(Keys.FIRSTLY_SELECTION, true);
            isRedo = getIntent().getBooleanExtra(Keys.IS_REDO, false);
        }

        handler = new DbHandler(getContentResolver());

        missionDueTextView = (TextView) findViewById(R.id.missionDueTextView);
        taskDataSizeTextView = (TextView) findViewById(R.id.taskDataSizeTextView);
        dueInTextView = (TextView) findViewById(R.id.dueInTextView);
        closingQuestionText = (TextView) findViewById(R.id.closingQuestionText);

        Button recheckAnswerButton = (Button) findViewById(R.id.recheckTaskButton);
        recheckAnswerButton.setOnClickListener(this);
        recheckAnswerButton.setVisibility(firstlySelection ? View.VISIBLE : View.GONE);

        sendNowButton = (Button) findViewById(R.id.sendNowButton);
        sendNowButton.setOnClickListener(this);

        sendLaterButton = (Button) findViewById(R.id.sendLaterButton);
        sendLaterButton.setOnClickListener(this);

        setSupportProgressBarIndeterminateVisibility(false);

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

                        if (!isValidationLocationAdded(task)) {
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
                        closingQuestionText.setText(getString(R.string.task_has_not_yet_submitted,
                                "\n" + question.getQuestion()));

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
    public void onNetworkOperation(BaseOperation operation) {
        setSupportProgressBarIndeterminateVisibility(false);

        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
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
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
            sendNowButton.setEnabled(true);
        }
    }

    public void setTaskData(Task task) {
        taskDataSizeTextView.setText(String.format(Locale.US, "%.1f", filesSizeB / 1024) + " " + getString(R.string
                .task_data_size_mb));

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
        for (NotUploadedFile notUploadedFile : notUploadedFiles) {
            notUploadedFile.setUse3G(use3G);
            notUploadedFile.setLatitudeToValidation(task.getLatitudeToValidation());
            notUploadedFile.setLongitudeToValidation(task.getLongitudeToValidation());

            FilesBL.insertNotUploadedFile(notUploadedFile);
        }
        startService(new Intent(TaskValidationActivity.this, UploadFileService.class).setAction(Keys
                .ACTION_CHECK_NOT_UPLOADED_FILES));
    }

    private void validateTask(final int taskId, final int missionId, final Double latitude, final Double longitude) {
        setSupportProgressBarIndeterminateVisibility(true);

        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        MatrixLocationManager.getAddressByLocation(location, new MatrixLocationManager.GetAddressListener() {
            @Override
            public void onGetAddressSuccess(Location location, String countryName, String cityName, String districtName) {

                sendNetworkOperation(apiFacade.getValidateTaskOperation(taskId, missionId, latitude, longitude,
                        cityName));
            }
        });

    }

    public void sendTextAnswers() {
        if ((UIUtils.is3G(this) && !preferencesManager.getUseOnlyWiFiConnaction()) || UIUtils.isWiFi(this)) {
            setSupportProgressBarIndeterminateVisibility(true);

            apiFacade.sendAnswers(this, answerListToSend, missionId);
        } else {
            DialogUtils.showTurnOnWifiDialog(this);
        }
    }

    private void sendAnswerTextsSuccess() {
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
            validateTask(task.getId(), task.getMissionId(), task.getLatitudeToValidation(),
                    task.getLongitudeToValidation());
        }
    }

    /**
     * Check a lot of system setting before data upload. If some thing wrong show Dialog
     *
     * @return
     */
    public boolean isReadyToSend() {
        boolean result = UIUtils.isOnline(this) && UIUtils.isGpsEnabled(this)
                && preferencesManager.getUseLocationServices()
                && !UIUtils.isMockLocationEnabled(this);
        if (!UIUtils.isOnline(this)) {
            DialogUtils.showNetworkDialog(this);
        } else if (lm.getLocation() == null || !UIUtils.isGpsEnabled(this)
                || !UIUtils.isNetworkEnabled(this) || !preferencesManager.getUseLocationServices()) {
            DialogUtils.showLocationDialog(this, true);
        } else if (UIUtils.isMockLocationEnabled(this)) {
            DialogUtils.showMockLocationDialog(this, true);
        }

        return result;
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
                TasksBL.updateTaskStatusId(taskId, task.getMissionId(), Task.TaskStatusId.STARTED.getStatusId());

                startActivity(IntentUtils.getQuestionsIntent(this, task.getId(), task.getMissionId()));
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
            if (!isValidationLocationAdded(task)) {
                if (hasFile) {
                    AnswersBL.savePhotoVideoAnswersAverageLocation(task, answerListToSend);

                    sendAnswers();
                } else {
                    MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
                        @Override
                        public void getLocationStart() {
                            setSupportProgressBarIndeterminateVisibility(true);
                        }

                        @Override
                        public void getLocationInProcess() {
                        }

                        @Override
                        public void getLocationSuccess(Location location) {
                            if (!isFinishing()) {
                                saveLocationOfTaskToDb(task, location);

                                setSupportProgressBarIndeterminateVisibility(false);

                                sendAnswers();
                            } else {
                                saveLocationOfTaskToDb(task, location);
                            }
                        }

                        @Override
                        public void getLocationFail(String errorText) {
                            if (!isFinishing()) {
                                sendNowButton.setEnabled(true);
                                UIUtils.showSimpleToast(TaskValidationActivity.this, errorText);
                            }
                        }
                    });
                }
            } else {
                sendAnswers();
            }
        }
    }

    /**
     * Postpone uploading and put it into local DB with flag.
     */
    public void sendLaterButtonClick() {
        if (!isValidationLocationAdded(task)) {
            if (hasFile) {
                AnswersBL.savePhotoVideoAnswersAverageLocation(task, answerListToSend);

                finishActivity();
            } else {
                MatrixLocationManager.getCurrentLocation(false, new MatrixLocationManager.GetCurrentLocationListener() {
                    @Override
                    public void getLocationStart() {
                        setSupportProgressBarIndeterminateVisibility(true);
                    }

                    @Override
                    public void getLocationInProcess() {
                    }

                    @Override
                    public void getLocationSuccess(Location location) {
                        if (!isFinishing()) {
                            saveLocationOfTaskToDb(task, location);

                            setSupportProgressBarIndeterminateVisibility(false);

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
            setSupportProgressBarIndeterminateVisibility(true);
            apiFacade.startTask(this, task.getId(), task.getMissionId());
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

    /**
     * Save Location of task to DB
     *
     * @param task
     * @param location
     */
    private void saveLocationOfTaskToDb(Task task, Location location) {
        task.setLatitudeToValidation(location.getLatitude());
        task.setLongitudeToValidation(location.getLongitude());
        TasksBL.updateTask(task);
    }
}
