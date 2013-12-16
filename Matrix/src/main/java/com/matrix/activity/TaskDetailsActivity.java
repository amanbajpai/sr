package com.matrix.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.matrix.BaseActivity;
import com.matrix.Keys;
import com.matrix.R;
import com.matrix.bl.SurveysBL;
import com.matrix.bl.TasksBL;
import com.matrix.db.SurveyDbSchema;
import com.matrix.db.TaskDbSchema;
import com.matrix.db.entity.BookTaskResponse;
import com.matrix.db.entity.Survey;
import com.matrix.db.entity.Task;
import com.matrix.dialog.BookTaskSuccessDialog;
import com.matrix.helpers.APIFacade;
import com.matrix.net.BaseOperation;
import com.matrix.net.NetworkOperationListenerInterface;
import com.matrix.utils.UIUtils;

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

        findViewById(R.id.bookButton).setOnClickListener(this);
        findViewById(R.id.hideTaskButton).setOnClickListener(this);
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
            }
        } else {
            UIUtils.showSimpleToast(TaskDetailsActivity.this, "Server Error. Response Code: " + operation.getResponseStatusCode());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookButton:
                //apiFacade.bookTask(this, taskId);
                new BookTaskSuccessDialog(this, UIUtils.longToString(UIUtils.isoTimeToLong(survey.getEndDateTime()), 3));
                break;
            case R.id.hideTaskButton:
                TasksBL.setHideTaskOnMapByID(handler, task.getId(), true);
                break;
            case R.id.showTaskOnMapButton:
                TasksBL.setHideTaskOnMapByID(handler, task.getId(), false);
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
