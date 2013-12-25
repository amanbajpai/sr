package com.ros.smartrocket.activity;

import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.ros.smartrocket.BaseActivity;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.SurveysBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.SurveyDbSchema;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.BookTaskResponse;
import com.ros.smartrocket.db.entity.Survey;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.dialog.WithdrawTaskDialog;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity for view Task detail information
 */
public class TaskDetailsActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    public final static String TAG = TaskDetailsActivity.class.getSimpleName();
    private APIFacade apiFacade = APIFacade.getInstance();

    private AsyncQueryHandler handler;

    private Integer taskId;
    private Task task = new Task();
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
        setTitle(R.string.task_detail_title);

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
        }

        handler = new DbHandler(getContentResolver());

        EasyTracker.getInstance(this).send(MapBuilder.createEvent(TAG, "onCreate", "deviceId=" + UIUtils.getDeviceId(this), (long) 0).build());

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

        findViewById(R.id.showTaskOnMapButton).setOnClickListener(this);

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

                    setTaskData(task);
                    SurveysBL.getSurveyFromDB(handler, task.getSurveyId());
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
        if (operation.getResponseStatusCode() == 200) {
            if (Keys.BOOK_TASK_OPERATION_TAG.equals(operation.getTag())) {
                BookTaskResponse bookTaskResponse = (BookTaskResponse) operation.getResponseEntities().get(0);
                if (bookTaskResponse.getState()) {
                    UIUtils.showSimpleToast(TaskDetailsActivity.this, R.string.success);
                } else {
                    UIUtils.showSimpleToast(TaskDetailsActivity.this, R.string.error);
                }
                //TODO Set started for task
                setButtonsSettings(task);
            }
        } else {
            UIUtils.showSimpleToast(this, operation.getResponseError());
        }
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

        if (UIUtils.isTrue(task.getBooked())) {
            withdrawTaskButton.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(task.getStarted())) {
                startTaskButton.setVisibility(View.VISIBLE);
            } else {
                continueTaskButton.setVisibility(View.VISIBLE);
            }

        } else {
            bookTaskButton.setVisibility(View.VISIBLE);

            if (UIUtils.isTrue(task.getIsHide())) {
                showTaskButton.setVisibility(View.VISIBLE);
            } else {
                hideTaskButton.setVisibility(View.VISIBLE);
            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookTaskButton:
                //apiFacade.bookTask(this, taskId);

                String dateTime = UIUtils.longToString(UIUtils.isoTimeToLong(survey.getEndDateTime()), 3);
                new BookTaskSuccessDialog(this, dateTime, new BookTaskSuccessDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed(Dialog dialog) {
                        //TODO Remove booked task
                        task.setBooked(false);
                        task.setStarted("");
                        setButtonsSettings(task);
                        TasksBL.setTask(handler, task);
                    }

                    @Override
                    public void onStartLaterButtonPressed(Dialog dialog) {
                        task.setBooked(true);
                        task.setStarted("");
                        setButtonsSettings(task);
                        TasksBL.setTask(handler, task);
                    }

                    @Override
                    public void onStartNowButtonPressed(Dialog dialog) {
                        //TODO Start question screen
                        task.setBooked(true);
                        task.setStarted(UIUtils.longToString(Calendar.getInstance().getTimeInMillis(), 3));
                        setButtonsSettings(task);
                        TasksBL.setTask(handler, task);
                        //startActivity(IntentUtils.getQuestionsIntent(TaskDetailsActivity.this, task.getSurveyId()));
                    }
                });
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
                //apiFacade.bookTask(this, taskId);
                String endDateTime = UIUtils.longToString(UIUtils.isoTimeToLong(survey.getEndDateTime()), 3);
                new WithdrawTaskDialog(this, endDateTime, new WithdrawTaskDialog.DialogButtonClickListener() {
                    @Override
                    public void onNoButtonPressed(Dialog dialog) {
                    }

                    @Override
                    public void onYesButtonPressed(Dialog dialog) {
                        //TODO Remove book for this task. Refresh buttons
                        task.setStarted("");
                        task.setBooked(false);
                        setButtonsSettings(task);
                        TasksBL.setTask(handler, task);
                    }
                });
                break;
            case R.id.startTaskButton:
                task.setStarted(UIUtils.longToString(Calendar.getInstance().getTimeInMillis(), 3));
                setButtonsSettings(task);
                TasksBL.setTask(handler, task);
                break;
            case R.id.continueTaskButton:
                //TODO Start question screen
                //startActivity(IntentUtils.getQuestionsIntent(this, task.getSurveyId()));
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
