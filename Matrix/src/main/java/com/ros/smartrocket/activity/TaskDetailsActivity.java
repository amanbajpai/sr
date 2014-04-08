package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.SurveyDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.dialog.WithdrawTaskDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.location.MatrixLocationManager;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for view Task detail information
 */
public class TaskDetailsActivity extends BaseActivity implements View.OnClickListener,
        NetworkOperationListenerInterface {
    private static final String TAG = TaskDetailsActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private AsyncQueryHandler handler;

    private Integer taskId;
    private Task task;
    private Survey survey = new Survey();

    private TextView taskName;
    private TextView startTimeTextView;
    private TextView deadlineTimeTextView;
    private TextView expiryTimeTextView;
    private TextView taskPrice;
    private TextView taskExp;
    private TextView taskDistance;
    private TextView taskAddress;
    private TextView taskDescription;
    private TextView taskComposition;
    private Button bookTaskButton;
    private Button startTaskButton;
    private Button hideTaskButton;
    private Button showTaskButton;
    private Button withdrawTaskButton;
    private Button continueTaskButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_task_details);

        //getSupportActionBar().setIcon(R.drawable.ic_launcher);

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
        }

        handler = new DbHandler(getContentResolver());

        taskName = (TextView) findViewById(R.id.taskName);
        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        expiryTimeTextView = (TextView) findViewById(R.id.expiryTimeTextView);
        taskPrice = (TextView) findViewById(R.id.taskPrice);
        taskExp = (TextView) findViewById(R.id.taskExp);
        taskDistance = (TextView) findViewById(R.id.taskDistance);
        taskDescription = (TextView) findViewById(R.id.taskDescription);
        taskAddress = (TextView) findViewById(R.id.taskAddress);
        taskComposition = (TextView) findViewById(R.id.taskComposition);

        bookTaskButton = (Button) findViewById(R.id.bookTaskButton);
        bookTaskButton.setOnClickListener(this);
        startTaskButton = (Button) findViewById(R.id.startTaskButton);
        startTaskButton.setOnClickListener(this);
        hideTaskButton = (Button) findViewById(R.id.hideTaskButton);
        hideTaskButton.setOnClickListener(this);
        showTaskButton = (Button) findViewById(R.id.showTaskButton);
        showTaskButton.setOnClickListener(this);
        withdrawTaskButton = (Button) findViewById(R.id.withdrawTaskButton);
        withdrawTaskButton.setOnClickListener(this);
        continueTaskButton = (Button) findViewById(R.id.continueTaskButton);
        continueTaskButton.setOnClickListener(this);

        findViewById(R.id.mapImageView).setOnClickListener(this);

        findViewById(R.id.showTaskOnMapButton).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    if (cursor != null && cursor.getCount() > 0) {
                        if (task != null && (TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId
                                .scheduled
                                || TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.validation)) {
                            finish();
                            break;
                        }
                        task = TasksBL.convertCursorToTask(cursor);

                        setTaskData(task);
                        SurveysBL.getSurveyFromDB(handler, task.getSurveyId());
                    } else {
                        finish();
                    }
                    break;
                case SurveyDbSchema.Query.TOKEN_QUERY:
                    survey = SurveysBL.convertCursorToSurvey(cursor);

                    setSurveyData(survey);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
            if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                task.setStatusId(Task.TaskStatusId.claimed.getStatusId());
                task.setIsMy(true);

                String dateTime = UIUtils.longToString(UIUtils.isoTimeToLong(survey.getEndDateTime()), 5);
                new BookTaskSuccessDialog(this, dateTime, new BookTaskSuccessDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed(Dialog dialog) {
                        setSupportProgressBarIndeterminateVisibility(true);
                        apiFacade.unclaimTask(TaskDetailsActivity.this, task.getId());
                    }

                    @Override
                    public void onStartLaterButtonPressed(Dialog dialog) {
                    }

                    @Override
                    public void onStartNowButtonPressed(Dialog dialog) {
                        setSupportProgressBarIndeterminateVisibility(true);
                        apiFacade.startTask(TaskDetailsActivity.this, task.getId());

                    }
                });

                setButtonsSettings(task);
                TasksBL.updateTask(handler, task);
            } else if (Keys.UNCLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + task.getSurveyId() + "_"
                        + task.getId());

                task.setStatusId(Task.TaskStatusId.none.getStatusId());
                task.setStarted("");
                task.setIsMy(false);
                setButtonsSettings(task);
                TasksBL.updateTask(handler, task);

                QuestionsBL.removeQuestionsFromDB(TaskDetailsActivity.this, survey.getId(), task.getId());
                AnswersBL.removeAnswersByTaskId(TaskDetailsActivity.this, task.getId());

            } else if (Keys.START_TASK_OPERATION_TAG.equals(operation.getTag())) {
                task.setStatusId(Task.TaskStatusId.started.getStatusId());
                task.setStarted(UIUtils.longToString(Calendar.getInstance().getTimeInMillis(), 2));
                setButtonsSettings(task);
                TasksBL.updateTask(handler, task);
                startActivity(IntentUtils.getQuestionsIntent(TaskDetailsActivity.this, task.getSurveyId(),
                        task.getId()));
            }
        } else {
            if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())
                    && operation.getResponseErrorCode() == BaseNetworkService.MAXIMUM_MISSION_ERROR_CODE) {
                DialogUtils.showMaximumMissionDialog(this);
            } else {
                UIUtils.showSimpleToast(this, operation.getResponseError());
            }
        }
        setSupportProgressBarIndeterminateVisibility(false);
    }

    public void setTaskData(Task task) {
        taskName.setText(task.getName());
        taskDescription.setText(task.getDescription());
        taskComposition.setText("-");
        taskPrice.setText(Html.fromHtml(String.format(getString(R.string.task_price), String.format(Locale.US, "%.1f",
                task.getPrice()))));

        taskDistance.setText(Html.fromHtml(UIUtils.convertMToKm(this, task.getDistance(), R.string.task_distance)));
        taskAddress.setText(task.getAddress());

        setButtonsSettings(task);
    }

    public void setSurveyData(Survey survey) {
        long startTimeInMillisecond = UIUtils.isoTimeToLong(survey.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(survey.getEndDateTime());

        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));
        deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
        //TODO Set expiry time
        expiryTimeTextView.setText("-");

        //TODO Set EXP
        taskExp.setText(Html.fromHtml(String.format(getString(R.string.task_exp), String.format(Locale.US, "%,d",
                130))));
    }

    public void setButtonsSettings(Task task) {
        bookTaskButton.setVisibility(View.GONE);
        startTaskButton.setVisibility(View.GONE);
        hideTaskButton.setVisibility(View.GONE);
        showTaskButton.setVisibility(View.GONE);
        withdrawTaskButton.setVisibility(View.GONE);
        continueTaskButton.setVisibility(View.GONE);

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case none:
                bookTaskButton.setVisibility(View.VISIBLE);
                if (UIUtils.isTrue(task.getIsHide())) {
                    showTaskButton.setVisibility(View.VISIBLE);
                } else {
                    hideTaskButton.setVisibility(View.VISIBLE);
                }
                break;
            case claimed:
                withdrawTaskButton.setVisibility(View.VISIBLE);
                startTaskButton.setVisibility(View.VISIBLE);
                break;
            case started:
                withdrawTaskButton.setVisibility(View.VISIBLE);
                continueTaskButton.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookTaskButton:
                setSupportProgressBarIndeterminateVisibility(true);

                Location location = lm.getLocation();

                apiFacade.claimTask(this, taskId, location.getLatitude(), location.getLongitude());
                break;
            case R.id.hideTaskButton:
                task.setIsHide(true);
                setButtonsSettings(task);
                TasksBL.setHideTaskOnMapByID(handler, task.getId(), true);
                break;
            case R.id.showTaskButton:
                task.setIsHide(false);
                setButtonsSettings(task);
                TasksBL.setHideTaskOnMapByID(handler, task.getId(), false);
                break;
            case R.id.showTaskOnMapButton:
                TasksBL.setHideTaskOnMapByID(handler, task.getId(), false);
                task.setIsHide(false);
                setButtonsSettings(task);
                break;
            case R.id.withdrawTaskButton:
                String endDateTime = UIUtils.longToString(UIUtils.isoTimeToLong(survey.getEndDateTime()), 3);
                new WithdrawTaskDialog(this, endDateTime, new WithdrawTaskDialog.DialogButtonClickListener() {
                    @Override
                    public void onNoButtonPressed(Dialog dialog) {
                    }

                    @Override
                    public void onYesButtonPressed(Dialog dialog) {
                        setSupportProgressBarIndeterminateVisibility(true);
                        apiFacade.unclaimTask(TaskDetailsActivity.this, task.getId());
                    }
                });
                break;
            case R.id.startTaskButton:
                setSupportProgressBarIndeterminateVisibility(true);
                apiFacade.startTask(TaskDetailsActivity.this, task.getId());
                break;
            case R.id.continueTaskButton:
                switch (TasksBL.getTaskStatusType(task.getStatusId())) {
                    case claimed:
                    case started:
                        startActivity(IntentUtils.getQuestionsIntent(this, task.getSurveyId(), task.getId()));
                        break;
                    case scheduled:
                        startActivity(IntentUtils.getTaskValidationIntent(this, task.getId()));
                        break;
                    default:
                        break;
                }
                break;

            case R.id.mapImageView:
                Bundle bundle = new Bundle();
                bundle.putInt(Keys.MAP_VIEWITEM_ID, task.getId());
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.SINGLETASK.toString());
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
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
        ((TextView) view.findViewById(R.id.titleTextView)).setText(R.string.task_detail_title);

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
