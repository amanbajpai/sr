package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
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
    private boolean firstlySelection;
    private boolean isRedo;
    private Task task = new Task();

    private AsyncQueryHandler handler;
    private List<NotUploadedFile> notUploadedFiles = new ArrayList<NotUploadedFile>();
    private List<Answer> answerListToSend = new ArrayList<Answer>();
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

        TasksBL.getTaskFromDBbyID(handler, taskId);
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    task = TasksBL.convertCursorToTask(cursor);

                    if (actionBarView != null) {
                        ((TextView) actionBarView.findViewById(R.id.titleTextView)).setText(task.getName());
                    }

                    long endDateTime = UIUtils.isoTimeToLong(task.getEndDateTime());

                    answerListToSend = AnswersBL.getAnswersListToSend(task.getId());
                    notUploadedFiles = AnswersBL.getTaskFilesListToUpload(task.getId(), task.getName(), endDateTime);
                    filesSizeB = AnswersBL.getTaskFilesSizeMb(notUploadedFiles);

                    setTaskData(task);
                    if (firstlySelection) {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                        QuestionsBL.getClosingStatementQuestionFromDB(handler, task.getWaveId(), task.getId());
                    } else {
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        sendNowButton.setBackgroundResource(R.drawable.button_blue_selector);
                        sendLaterButton.setBackgroundResource(R.drawable.button_blue_selector);

                        UIUtils.setActionBarBackground(TaskValidationActivity.this, task.getStatusId());
                        closingQuestionText.setText(R.string.task_has_not_yet_submitted2);
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

            } else if (Keys.SEND_ANSWERS_OPERATION_TAG.equals(operation.getTag())) {
                sendAnswerTextsSuccess();

            } else if (Keys.VALIDATE_TASK_OPERATION_TAG.equals(operation.getTag())) {
                task.setSubmittedAt(UIUtils.longToString(calendar.getTimeInMillis(), 2));
                task.setStatusId(Task.TaskStatusId.VALIDATION.getStatusId());
                TasksBL.updateTask(handler, task);

                QuestionsBL.removeQuestionsFromDB(this, task.getWaveId(), task.getId());

                finishActivity();
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }

    }

    public void setTaskData(Task task) {
        long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();

        taskDataSizeTextView.setText(String.format(Locale.US, "%.1f", filesSizeB / 1024) + " " + getString(R.string
                .task_data_size_mb));

        long missionDueMillisecond;

        if (isRedo) {
            long reDoTimeInMillisecond = UIUtils.isoTimeToLong(task.getRedoDate());
            missionDueMillisecond = reDoTimeInMillisecond + timeoutInMillisecond;
        } else {
            long claimTimeInMillisecond = UIUtils.isoTimeToLong(task.getClaimed());
            missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
        }

        long dueInMillisecond = missionDueMillisecond - calendar.getTimeInMillis();

        dueInTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, dueInMillisecond));
        missionDueTextView.setText(UIUtils.longToString(missionDueMillisecond, 3));
    }

    public void setFilesToUploadDbAndStartUpload(Boolean use3G) {
        //Add files data to DB and start upload
        for (NotUploadedFile notUploadedFile : notUploadedFiles) {
            notUploadedFile.setUse3G(use3G);
            FilesBL.insertNotUploadedFile(notUploadedFile);
        }
        startService(new Intent(TaskValidationActivity.this, UploadFileService.class).setAction(Keys
                .ACTION_CHECK_NOT_UPLOADED_FILES));
    }

    private void validateTask(final int taskId) {
        setSupportProgressBarIndeterminateVisibility(true);

        MatrixLocationManager.getCurrentLocation(new MatrixLocationManager.GetCurrentLocationListener() {
            @Override
            public void getLocationStart() {
            }

            @Override
            public void getLocationInProcess() {
            }

            @Override
            public void getLocationSuccess(Location location) {
                sendNetworkOperation(apiFacade.getValidateTaskOperation(taskId,
                        location.getLatitude(), location.getLongitude()));
            }
        });
    }

    public void sendTextAnswers() {
        if ((UIUtils.is3G(this) && !preferencesManager.getUseOnlyWiFiConnaction()) || UIUtils.isWiFi(this)) {
            setSupportProgressBarIndeterminateVisibility(true);

            apiFacade.sendAnswers(this, answerListToSend);
        } else {
            DialogUtils.showTurnOnWifiDialog(this);
        }
    }

    private void sendAnswerTextsSuccess() {
        TasksBL.updateTaskStatusId(task.getId(), Task.TaskStatusId.COMPLETED.getStatusId());

        if (filesSizeB > 0) {
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
            validateTask(task.getId());
        }
    }

    public void finishActivity() {
        if (firstlySelection) {
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + task.getWaveId()
                    + "_" + taskId);

            startActivity(IntentUtils.getMainActivityIntent(this));
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recheckTaskButton:
                TasksBL.updateTaskStatusId(taskId, Task.TaskStatusId.STARTED.getStatusId());

                startActivity(IntentUtils.getQuestionsIntent(this, taskId));
                finish();
                break;
            case R.id.sendNowButton:

                if (task.getStartedStatusSent()) {
                    sendTextAnswers();
                } else {
                    setSupportProgressBarIndeterminateVisibility(true);
                    apiFacade.startTask(this, task.getId());
                }
                break;
            case R.id.sendLaterButton:
                finishActivity();
                break;
            default:
                break;
        }
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

}
