package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Locale;

public class TaskValidationActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    //private static final String TAG = TaskValidationActivity.class.getSimpleName();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private APIFacade apiFacade = APIFacade.getInstance();
    private TextView expiryDateTextView;
    private TextView taskDataSizeTextView;
    private TextView closingQuestionText;
    private LinearLayout closingQuestionTextLayout;

    private int taskId;
    private boolean showRecheckAnswerButton;
    private Task task = new Task();

    private AsyncQueryHandler handler;
    private ArrayList<NotUploadedFile> notUploadedFiles = new ArrayList<NotUploadedFile>();
    private ArrayList<Answer> answerListToSend = new ArrayList<Answer>();
    private float filesSizeB = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_task_validation);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            showRecheckAnswerButton = getIntent().getBooleanExtra(Keys.SHOW_RECHECK_ANSWERS_BUTTON, true);
        }

        handler = new DbHandler(getContentResolver());

        expiryDateTextView = (TextView) findViewById(R.id.expiryDateTextView);
        taskDataSizeTextView = (TextView) findViewById(R.id.taskDataSizeTextView);
        closingQuestionTextLayout = (LinearLayout) findViewById(R.id.closingQuestionTextLayout);
        //closingQuestionTextLayout.setVisibility(showRecheckAnswerButton ? View.VISIBLE : View.GONE);
        closingQuestionText = (TextView) findViewById(R.id.closingQuestionText);

        Button recheckAnswerButton = (Button) findViewById(R.id.recheckTaskButton);
        recheckAnswerButton.setOnClickListener(this);
        recheckAnswerButton.setVisibility(showRecheckAnswerButton ? View.VISIBLE : View.GONE);

        findViewById(R.id.sendNowButton).setOnClickListener(this);
        findViewById(R.id.sendLaterButton).setOnClickListener(this);

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

                    long endDateTime = UIUtils.isoTimeToLong(task.getEndDateTime());

                    answerListToSend = AnswersBL.getAnswersListToSend(task.getId());
                    notUploadedFiles = AnswersBL.getTaskFilesListToUpload(task.getId(), endDateTime);
                    filesSizeB = AnswersBL.getTaskFilesSizeMb(notUploadedFiles);

                    setTaskData(task);
                    //SurveysBL.getSurveyFromDB(handler, task.getSurveyId());
                    if (showRecheckAnswerButton) {
                        QuestionsBL.getClosingStatementQuestionFromDB(handler, task.getSurveyId(), task.getId());
                    } else {
                        closingQuestionText.setText(R.string.task_has_not_yet_submitted);
                        closingQuestionTextLayout.setVisibility(View.VISIBLE);
                    }
                    break;
                case QuestionDbSchema.Query.TOKEN_QUERY:
                    ArrayList<Question> questions = QuestionsBL.convertCursorToQuestionList(cursor);
                    if (questions.size() > 0) {
                        Question question = questions.get(0);
                        closingQuestionText.setText(question.getQuestion());
                        closingQuestionTextLayout.setVisibility(View.VISIBLE);
                    } else {
                        closingQuestionTextLayout.setVisibility(View.GONE);
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
            if (Keys.SEND_ANSWERS_OPERATION_TAG.equals(operation.getTag())) {
                sendAnswerTextsSuccess();

            } else if (Keys.VALIDATE_TASK_OPERATION_TAG.equals(operation.getTag())) {
                QuestionsBL.removeQuestionsFromDB(this, task.getSurveyId(), task.getId());
                TasksBL.updateTaskStatusId(task.getId(), Task.TaskStatusId.validation.getStatusId());

                finishActivity();
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
        setSupportProgressBarIndeterminateVisibility(false);
    }

    public void setTaskData(Task task) {
        long expiryTimeLong = UIUtils.isoTimeToLong(task.getEndDateTime());

        expiryDateTextView.setText(UIUtils.longToString(expiryTimeLong, 6));
        taskDataSizeTextView.setText(String.format(Locale.US, "%.1f", filesSizeB / 1024) + " " + getString(R.string
                .task_data_size_mb));
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

        Location location = lm.getLocation();
        if (location != null) {
            sendNetworkOperation(apiFacade.getValidateTaskOperation(taskId, location.getLatitude(),
                    location.getLongitude()));
        } else {
            lm.getLocationAsync(new MatrixLocationManager.ILocationUpdate() {
                @Override
                public void onUpdate(Location location) {
                    sendNetworkOperation(apiFacade.getValidateTaskOperation(taskId, location.getLatitude(),
                            location.getLongitude()));
                }
            });
        }

    }

    private void sendAnswerTextsSuccess() {
        TasksBL.updateTaskStatusId(task.getId(), Task.TaskStatusId.completed.getStatusId());
        //QuestionsBL.removeQuestionsFromDB(TaskValidationActivity.this, task.getSurveyId(), task.getId());

        if (filesSizeB > 0) {
            if (UIUtils.is3G(this) && filesSizeB / 1024 > preferencesManager.get3GUploadTaskLimit()) {
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
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + task.getSurveyId() + "_" + taskId);

        startActivity(IntentUtils.getMainActivityIntent(this));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recheckTaskButton:
                TasksBL.updateTaskStatusId(taskId, Task.TaskStatusId.started.getStatusId());

                startActivity(IntentUtils.getQuestionsIntent(this, task.getSurveyId(), taskId));
                finish();
                break;
            case R.id.sendNowButton:
                setSupportProgressBarIndeterminateVisibility(true);

                apiFacade.sendAnswers(this, answerListToSend);
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
                finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.validation_title);

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
