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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.dialog.CustomProgressDialog;
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
    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private Calendar calendar = Calendar.getInstance();
    private AsyncQueryHandler handler;

    private Integer taskId;
    private Task task;
    private Wave wave = new Wave();

    private TextView startTimeTextView;
    private TextView deadlineTimeTextView;
    private TextView dueTextView;
    private TextView expireText;
    private TextView taskPrice;
    private TextView taskExp;
    private TextView textQuestionsCount;
    private TextView photoQuestionsCount;
    private TextView taskDistance;
    private TextView deadlineTimeText;

    private LinearLayout addressLayout;
    private LinearLayout descriptionLayout;

    private TextView taskAddress;
    private TextView locationName;
    private TextView taskDescription;

    private Button bookTaskButton;
    private Button startTaskButton;
    private Button hideTaskButton;
    private Button showTaskButton;
    private Button withdrawTaskButton;
    private Button continueTaskButton;

    private View actionBarView;

    private LinearLayout startTimeLayout;
    private LinearLayout deadlineTimeLayout;
    private LinearLayout expireTimeLayout;

    private TextView titleTextView;

    private CustomProgressDialog progressDialog;

    public TaskDetailsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_task_details);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
        }

        progressDialog = CustomProgressDialog.show(this);
        progressDialog.setCancelable(false);
        progressDialog.hide();

        handler = new DbHandler(getContentResolver());

        startTimeLayout = (LinearLayout) findViewById(R.id.startTimeLayout);
        deadlineTimeLayout = (LinearLayout) findViewById(R.id.deadlineTimeLayout);
        expireTimeLayout = (LinearLayout) findViewById(R.id.expireTimeLayout);

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        dueTextView = (TextView) findViewById(R.id.dueTextView);
        deadlineTimeText = (TextView) findViewById(R.id.deadlineTimeText);
        expireText = (TextView) findViewById(R.id.expireText);

        taskPrice = (TextView) findViewById(R.id.taskPrice);
        taskExp = (TextView) findViewById(R.id.taskExp);
        textQuestionsCount = (TextView) findViewById(R.id.textQuestionsCount);
        photoQuestionsCount = (TextView) findViewById(R.id.photoQuestionsCount);
        taskDistance = (TextView) findViewById(R.id.taskDistance);

        descriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);

        taskDescription = (TextView) findViewById(R.id.taskDescription);
        locationName = (TextView) findViewById(R.id.locationName);
        taskAddress = (TextView) findViewById(R.id.taskAddress);

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
                        task = TasksBL.convertCursorToTask(cursor);

                        setTaskData(task);
                        WavesBL.getWaveFromDB(handler, task.getWaveId());
                    } else {
                        finish();
                    }
                    break;
                case WaveDbSchema.Query.TOKEN_QUERY:
                    wave = WavesBL.convertCursorToWave(cursor);

                    setWaveData(wave);
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

                Location location = lm.getLocation();

                apiFacade.claimTask(this, taskId, location.getLatitude(), location.getLongitude());
            } else if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.hide();

                long claimTimeInMillisecond = calendar.getTimeInMillis();
                task.setStatusId(Task.TaskStatusId.claimed.getStatusId());
                task.setIsMy(true);
                task.setClaimed(UIUtils.longToString(claimTimeInMillisecond, 2));
                task.setLongClaimDateTime(claimTimeInMillisecond);

                TasksBL.updateTask(handler, task);

                long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();
                String dateTime = UIUtils.longToString(claimTimeInMillisecond + timeoutInMillisecond, 3);

                new BookTaskSuccessDialog(this, dateTime, new BookTaskSuccessDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed(Dialog dialog) {
                        progressDialog.show();
                        apiFacade.unclaimTask(TaskDetailsActivity.this, task.getId());
                    }

                    @Override
                    public void onStartLaterButtonPressed(Dialog dialog) {
                        setButtonsSettings(task);
                        startActivity(IntentUtils.getMainActivityIntent(TaskDetailsActivity.this));
                        finish();
                    }

                    @Override
                    public void onStartNowButtonPressed(Dialog dialog) {
                        progressDialog.show();
                        setButtonsSettings(task);
                        apiFacade.startTask(TaskDetailsActivity.this, task.getId());

                    }
                });

            } else if (Keys.UNCLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.hide();

                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + task.getWaveId() + "_"
                        + task.getId());

                task.setStatusId(Task.TaskStatusId.none.getStatusId());
                task.setStarted("");
                task.setIsMy(false);
                setButtonsSettings(task);
                TasksBL.updateTask(handler, task);

                QuestionsBL.removeQuestionsFromDB(TaskDetailsActivity.this, wave.getId(), task.getId());
                AnswersBL.removeAnswersByTaskId(TaskDetailsActivity.this, task.getId());

                startActivity(IntentUtils.getMainActivityIntent(this));

            } else if (Keys.START_TASK_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.hide();

                changeStatusToStartedAndOpenQuestion(true);
            }
        } else {
            if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag()) && operation.getResponseErrorCode() != null
                    && operation.getResponseErrorCode() == BaseNetworkService.MAXIMUM_MISSION_ERROR_CODE) {
                progressDialog.hide();
                DialogUtils.showMaximumMissionDialog(this);
            } else if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())
                    && operation.getResponseErrorCode() != null
                    && operation.getResponseErrorCode() == BaseNetworkService.MAXIMUM_CLAIM_PER_MISSION_ERROR_CODE) {
                progressDialog.hide();
                UIUtils.showSimpleToast(this, getString(R.string.task_no_longer_available));
            } else {
                progressDialog.hide();
                UIUtils.showSimpleToast(this, operation.getResponseError());
            }
        }
    }

    public void setTaskData(Task task) {
        startTimeLayout.setVisibility(task.getIsMy() ? View.GONE : View.VISIBLE);
        deadlineTimeLayout.setVisibility(View.VISIBLE);
        expireTimeLayout.setVisibility(View.VISIBLE);

        deadlineTimeText.setText(task.getIsMy() ? R.string.mission_due : R.string.deadline_time);
        expireText.setText(task.getIsMy() ? R.string.due_in : R.string.duration_time);

        taskPrice.setText(UIUtils.getBalanceOrPrice(this, task.getPrice(), task.getCurrencySign()));
        textQuestionsCount.setText(String.valueOf(task.getNoPhotoQuestionsCount()));
        photoQuestionsCount.setText(String.valueOf(task.getPhotoQuestionsCount()));
        taskExp.setText(String.format(Locale.US, "%.0f", task.getExperienceOffer()));

        descriptionLayout.setVisibility(TextUtils.isEmpty(task.getDescription()) ? View.GONE : View.VISIBLE);
        taskDescription.setText(task.getDescription());

        if (TextUtils.isEmpty(task.getLocationName()) && TextUtils.isEmpty(task.getAddress())) {
            addressLayout.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(task.getLocationName())) {
            locationName.setText(task.getLocationName());
        } else {
            locationName.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(task.getAddress())) {
            taskAddress.setText(task.getAddress());
        } else {
            taskAddress.setVisibility(View.GONE);
        }

        taskDistance.setText(UIUtils.convertMToKm(this, task.getDistance(), R.string.task_distance_away, false));

        setButtonsSettings(task);
    }

    public void setWaveData(Wave wave) {
        if (titleTextView != null) {
            titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));
        }

        long startTimeInMillisecond = UIUtils.isoTimeToLong(wave.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(wave.getEndDateTime());

        long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();
        long claimTimeInMillisecond = UIUtils.isoTimeToLong(task.getClaimed());
        long missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
        long dueInMillisecond = missionDueMillisecond - calendar.getTimeInMillis();

        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));

        if (task.getIsMy()) {
            deadlineTimeTextView.setText(UIUtils.longToString(missionDueMillisecond, 3));
            dueTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, dueInMillisecond));
        } else {
            deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
            dueTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));
        }

        if (actionBarView != null) {
            TextView titleTextView = (TextView) actionBarView.findViewById(R.id.titleTextView);
            titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));
        }
        //TODO Get wave type from server
        getSupportActionBar().setIcon(UIUtils.getWaveTypeActionBarIcon(1));
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

    public void changeStatusToStartedAndOpenQuestion(boolean startedStatusSent) {
        task.setStatusId(Task.TaskStatusId.started.getStatusId());
        task.setStarted(UIUtils.longToString(calendar.getTimeInMillis(), 2));
        task.setStartedStatusSent(startedStatusSent);

        setButtonsSettings(task);
        TasksBL.updateTask(handler, task);
        startActivity(IntentUtils.getQuestionsIntent(TaskDetailsActivity.this, task.getWaveId(),
                task.getId()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookTaskButton:
                progressDialog.show();

                apiFacade.getQuestions(this, task.getWaveId(), taskId);
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
            case R.id.withdrawTaskButton:
                long claimTimeInMillisecond = task.getLongClaimDateTime();
                long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();
                String dateTime = UIUtils.longToString(claimTimeInMillisecond + timeoutInMillisecond, 3);

                new WithdrawTaskDialog(this, dateTime, new WithdrawTaskDialog.DialogButtonClickListener() {
                    @Override
                    public void onNoButtonPressed(Dialog dialog) {
                    }

                    @Override
                    public void onYesButtonPressed(Dialog dialog) {
                        setSupportProgressBarIndeterminateVisibility(true);
                        progressDialog.show();
                        apiFacade.unclaimTask(TaskDetailsActivity.this, task.getId());
                    }
                });
                break;
            case R.id.startTaskButton:
                if (UIUtils.isOnline(this)) {
                    setSupportProgressBarIndeterminateVisibility(true);
                    progressDialog.show();
                    apiFacade.startTask(TaskDetailsActivity.this, task.getId());
                } else {
                    changeStatusToStartedAndOpenQuestion(false);
                }
                break;
            case R.id.continueTaskButton:
                switch (TasksBL.getTaskStatusType(task.getStatusId())) {
                    case claimed:
                    case started:
                        startActivity(IntentUtils.getQuestionsIntent(this, task.getWaveId(), task.getId()));
                        break;
                    case scheduled:
                        startActivity(IntentUtils.getTaskValidationIntent(this, task.getId(), false, false));
                        break;
                    default:
                        break;
                }
                break;

            case R.id.mapImageView:
                Bundle bundle = new Bundle();
                bundle.putInt(Keys.MAP_VIEW_ITEM_ID, task.getId());
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.SINGLE_TASK.toString());

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

        actionBarView = actionBar.getCustomView();

        if (wave != null) {
            titleTextView = (TextView) actionBarView.findViewById(R.id.titleTextView);
            titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));
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
