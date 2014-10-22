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
    private TextView expireTextView;
    private TextView expireText;
    private TextView taskPrice;
    private TextView taskExp;
    private TextView textQuestionsCount;
    private TextView photoQuestionsCount;
    private TextView taskDistance;
    private TextView deadlineTimeText;
    private TextView statusTextView;
    private TextView statusTimeText;
    private TextView statusTimeTextView;
    private TextView statusText;
    private TextView startTimeText;

    private LinearLayout addressLayout;
    private LinearLayout descriptionLayout;
    private TextView taskAddress;
    private View optionDivider;

    private TextView locationName;
    private TextView taskDescription;

    private LinearLayout buttonsLayout;

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
    private LinearLayout statusLayout;
    private LinearLayout statusTimeLayout;

    private LinearLayout timeLayout;
    private LinearLayout taskOptionsLayout;

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
        statusLayout = (LinearLayout) findViewById(R.id.statusLayout);
        statusTimeLayout = (LinearLayout) findViewById(R.id.statusTimeLayout);

        timeLayout = (LinearLayout) findViewById(R.id.timeLayout);
        taskOptionsLayout = (LinearLayout) findViewById(R.id.taskOptionsLayout);

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        expireTextView = (TextView) findViewById(R.id.expireTextView);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        statusTimeTextView = (TextView) findViewById(R.id.statusTimeTextView);

        statusText = (TextView) findViewById(R.id.statusText);
        startTimeText = (TextView) findViewById(R.id.startTimeText);
        deadlineTimeText = (TextView) findViewById(R.id.deadlineTimeText);
        expireText = (TextView) findViewById(R.id.expireText);
        statusTimeText = (TextView) findViewById(R.id.statusTimeText);

        taskPrice = (TextView) findViewById(R.id.taskPrice);
        taskExp = (TextView) findViewById(R.id.taskExp);
        textQuestionsCount = (TextView) findViewById(R.id.textQuestionsCount);
        photoQuestionsCount = (TextView) findViewById(R.id.photoQuestionsCount);
        taskDistance = (TextView) findViewById(R.id.taskDistance);
        optionDivider = findViewById(R.id.optionDivider);

        descriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);

        taskDescription = (TextView) findViewById(R.id.taskDescription);
        locationName = (TextView) findViewById(R.id.locationName);
        taskAddress = (TextView) findViewById(R.id.taskAddress);

        buttonsLayout = (LinearLayout) findViewById(R.id.buttonsLayout);

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

                MatrixLocationManager.getCurrentLocation(new MatrixLocationManager.GetCurrentLocationListener() {
                    @Override
                    public void getLocationStart() {
                        setSupportProgressBarIndeterminateVisibility(true);
                    }

                    @Override
                    public void getLocationInProcess() {
                    }

                    @Override
                    public void getLocationSuccess(Location location) {
                        apiFacade.claimTask(TaskDetailsActivity.this, taskId, location.getLatitude(), location.getLongitude());
                        setSupportProgressBarIndeterminateVisibility(false);
                    }
                });
            } else if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.hide();

                long startTimeInMillisecond = UIUtils.isoTimeToLong(wave.getStartDateTime());
                long preClaimedExpireInMillisecond = task.getLongPreClaimedTaskExpireAfterStart();
                long claimTimeInMillisecond = calendar.getTimeInMillis();
                long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();

                long missionDueMillisecond;
                if(TasksBL.isPreClaimTask(task)){
                    missionDueMillisecond = startTimeInMillisecond + preClaimedExpireInMillisecond;
                } else {
                    missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
                }

                task.setStatusId(Task.TaskStatusId.CLAIMED.getStatusId());
                task.setIsMy(true);
                task.setClaimed(UIUtils.longToString(claimTimeInMillisecond, 2));
                task.setLongClaimDateTime(claimTimeInMillisecond);

                TasksBL.updateTask(handler, task);

                String dateTime = UIUtils.longToString(missionDueMillisecond, 3);

                new BookTaskSuccessDialog(this, task, dateTime, new BookTaskSuccessDialog.DialogButtonClickListener() {
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

                task.setStatusId(Task.TaskStatusId.NONE.getStatusId());
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

        taskPrice.setText(UIUtils.getBalanceOrPrice(this, task.getPrice(), task.getCurrencySign(), null, null));
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

        setTaskDataByType(task);
        setColorTheme(task);
        setButtonsSettings(task);
    }

    public void setWaveData(Wave wave) {
        if (titleTextView != null) {
            titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));
        }

        long startTimeInMillisecond = UIUtils.isoTimeToLong(wave.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(wave.getEndDateTime());

        long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();
        long preClaimedExpireInMillisecond = task.getLongPreClaimedTaskExpireAfterStart();
        long claimTimeInMillisecond = task.getLongClaimDateTime();

        long missionDueMillisecond;
        if(TasksBL.isPreClaimTask(task)){
            missionDueMillisecond = startTimeInMillisecond + preClaimedExpireInMillisecond;
        } else {
            missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
        }
        long dueInMillisecond = missionDueMillisecond - calendar.getTimeInMillis();

        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));

        if (task.getIsMy()) {
            deadlineTimeTextView.setText(UIUtils.longToString(missionDueMillisecond, 3));
            expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, dueInMillisecond));
        } else {
            deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
            expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));
        }

        UIUtils.showWaveTypeActionBarIcon(this, wave.getIcon());
    }

    public void setTaskDataByType(Task task) {
        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case COMPLETED:
            case VALIDATION:
                startTimeLayout.setVisibility(View.GONE);
                deadlineTimeLayout.setVisibility(View.GONE);
                expireTimeLayout.setVisibility(View.GONE);

                statusLayout.setVisibility(View.VISIBLE);
                statusTextView.setText(getString(R.string.in_validation_task));

                if (!TextUtils.isEmpty(task.getSubmittedAt())) {
                    statusTimeLayout.setVisibility(View.VISIBLE);
                    statusTimeText.setText(getString(R.string.submitted_at));

                    long submittedTime = UIUtils.isoTimeToLong(task.getSubmittedAt());
                    statusTimeTextView.setText(UIUtils.longToString(submittedTime, 3));
                }

                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
                startTimeLayout.setVisibility(View.GONE);
                deadlineTimeLayout.setVisibility(View.GONE);
                expireTimeLayout.setVisibility(View.GONE);

                statusLayout.setVisibility(View.VISIBLE);
                statusTextView.setText(getString(R.string.approved_task));

                if (!TextUtils.isEmpty(task.getApprovedAt())) {
                    statusTimeLayout.setVisibility(View.VISIBLE);
                    statusTimeText.setText(getString(R.string.approved_at));

                    long approvedTime = UIUtils.isoTimeToLong(task.getApprovedAt());
                    statusTimeTextView.setText(UIUtils.longToString(approvedTime, 3));
                }
                break;
            case REJECTED:
                startTimeLayout.setVisibility(View.GONE);
                deadlineTimeLayout.setVisibility(View.GONE);
                expireTimeLayout.setVisibility(View.GONE);

                statusLayout.setVisibility(View.VISIBLE);
                statusTextView.setText(getString(R.string.rejected_task));

                if (!TextUtils.isEmpty(task.getRejectedAt())) {
                    statusTimeLayout.setVisibility(View.VISIBLE);
                    statusTimeText.setText(getString(R.string.rejected_at));

                    long rejectedTime = UIUtils.isoTimeToLong(task.getRejectedAt());
                    statusTimeTextView.setText(UIUtils.longToString(rejectedTime, 3));
                }
                break;
            default:
                break;
        }
    }

    public void setColorTheme(Task task) {
        UIUtils.setActionBarBackground(this, task);

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
            case CLAIMED:
            case STARTED:
                if (TasksBL.isPreClaimTask(task)) {
                    int violetLightColorResId = getResources().getColor(R.color.violet_light);
                    int violetDarkColorResId = getResources().getColor(R.color.violet_dark);
                    int violetColorResId = getResources().getColor(R.color.violet);
                    int whiteColorResId = getResources().getColor(R.color.white);

                    statusText.setTextColor(violetLightColorResId);
                    startTimeText.setTextColor(violetLightColorResId);
                    deadlineTimeText.setTextColor(violetLightColorResId);
                    expireText.setTextColor(violetLightColorResId);
                    statusTimeText.setTextColor(violetLightColorResId);

                    statusTextView.setTextColor(whiteColorResId);
                    startTimeTextView.setTextColor(whiteColorResId);
                    deadlineTimeTextView.setTextColor(whiteColorResId);
                    expireTextView.setTextColor(whiteColorResId);
                    statusTimeTextView.setTextColor(whiteColorResId);

                    taskOptionsLayout.setBackgroundColor(violetDarkColorResId);
                    optionDivider.setBackgroundColor(violetLightColorResId);
                    timeLayout.setBackgroundColor(violetColorResId);
                    buttonsLayout.setBackgroundColor(violetDarkColorResId);

                    taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_violet, 0, 0, 0);
                    taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_violet, 0, 0, 0);
                    textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_violet, 0, 0, 0);
                    photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_violet, 0, 0, 0);

                    bookTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    startTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    hideTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    showTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    withdrawTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    continueTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                } else {
                    taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.green_light));
                    optionDivider.setBackgroundColor(getResources().getColor(R.color.green_dark));

                    taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
                    taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
                    textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_green, 0, 0, 0);
                    photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_green, 0, 0, 0);
                }
                break;
            case PENDING:
            case SCHEDULED:
                taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.blue_light2));
                optionDivider.setBackgroundColor(getResources().getColor(R.color.blue));

                taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_blue, 0, 0, 0);
                taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_blue, 0, 0, 0);
                textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_blue, 0, 0, 0);
                photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_blue, 0, 0, 0);
                break;
            case COMPLETED:
                taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.grey));
                optionDivider.setBackgroundColor(getResources().getColor(R.color.grey_dark));

                taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_grey, 0, 0, 0);
                taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_grey, 0, 0, 0);
                textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_grey, 0, 0, 0);
                photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_grey, 0, 0, 0);
                break;
            case VALIDATION:
                taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.grey));
                optionDivider.setBackgroundColor(getResources().getColor(R.color.grey_dark));

                taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_lightgrey, 0, 0, 0);
                taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_lightgrey, 0, 0, 0);
                textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_lightgrey, 0, 0, 0);
                photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_lightgrey, 0, 0, 0);

                break;
            case RE_DO_TASK:
                taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.red));
                optionDivider.setBackgroundColor(getResources().getColor(R.color.red_dark));

                taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_red, 0, 0, 0);
                taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_red, 0, 0, 0);
                textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_red, 0, 0, 0);
                photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_red, 0, 0, 0);
                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
                taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.orange));
                optionDivider.setBackgroundColor(getResources().getColor(R.color.orange_dark));

                taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_gold, 0, 0, 0);
                taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_gold, 0, 0, 0);
                textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_gold, 0, 0, 0);
                photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_gold, 0, 0, 0);
                break;
            case REJECTED:
                taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.black_light));
                optionDivider.setBackgroundColor(getResources().getColor(R.color.black));

                taskPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_grey, 0, 0, 0);
                taskExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_grey, 0, 0, 0);
                textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_grey, 0, 0, 0);
                photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_grey, 0, 0, 0);
                break;
            default:
                break;
        }
    }

    public void setButtonsSettings(Task task) {
        buttonsLayout.setVisibility(View.GONE);
        bookTaskButton.setVisibility(View.GONE);
        startTaskButton.setVisibility(View.GONE);
        hideTaskButton.setVisibility(View.GONE);
        showTaskButton.setVisibility(View.GONE);
        withdrawTaskButton.setVisibility(View.GONE);
        continueTaskButton.setVisibility(View.GONE);

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
                buttonsLayout.setVisibility(View.VISIBLE);
                bookTaskButton.setVisibility(View.VISIBLE);
                if (UIUtils.isTrue(task.getIsHide())) {
                    showTaskButton.setVisibility(View.VISIBLE);
                } else {
                    hideTaskButton.setVisibility(View.VISIBLE);
                }
                break;
            case CLAIMED:
                buttonsLayout.setVisibility(View.VISIBLE);
                withdrawTaskButton.setVisibility(View.VISIBLE);
                startTaskButton.setVisibility(View.VISIBLE);
                if (TasksBL.isPreClaimTask(task)) {
                    startTaskButton.setEnabled(false);
                } else {
                    startTaskButton.setEnabled(true);
                }
                break;
            case STARTED:
                buttonsLayout.setVisibility(View.VISIBLE);
                withdrawTaskButton.setVisibility(View.VISIBLE);
                continueTaskButton.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public void changeStatusToStartedAndOpenQuestion(boolean startedStatusSent) {
        task.setStatusId(Task.TaskStatusId.STARTED.getStatusId());
        task.setStarted(UIUtils.longToString(calendar.getTimeInMillis(), 2));
        task.setStartedStatusSent(startedStatusSent);

        setButtonsSettings(task);
        TasksBL.updateTask(handler, task);
        startActivity(IntentUtils.getQuestionsIntent(TaskDetailsActivity.this, task.getId()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookTaskButton:
                bookTaskButtonClick();
                break;
            case R.id.hideTaskButton:
                hideTaskButtonClick();
                break;
            case R.id.showTaskButton:
                showTaskButtonClick();
                break;
            case R.id.withdrawTaskButton:
                withdrawTaskButtonClick();
                break;
            case R.id.startTaskButton:
                startTaskButtonClick();
                break;
            case R.id.continueTaskButton:
                continueTaskButtonClick();
                break;
            case R.id.mapImageView:
                mapImageViewClick();
                break;
            default:
                break;
        }
    }

    public void bookTaskButtonClick() {
        progressDialog.show();
        apiFacade.getQuestions(this, task.getWaveId(), taskId);
    }

    public void hideTaskButtonClick() {
        task.setIsHide(true);
        setButtonsSettings(task);
        TasksBL.setHideTaskOnMapByID(handler, task.getId(), true);
    }

    public void showTaskButtonClick() {
        task.setIsHide(false);
        setButtonsSettings(task);
        TasksBL.setHideTaskOnMapByID(handler, task.getId(), false);
    }

    public void withdrawTaskButtonClick() {
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
    }

    public void startTaskButtonClick() {
        if (UIUtils.isOnline(this)) {
            setSupportProgressBarIndeterminateVisibility(true);
            progressDialog.show();
            apiFacade.startTask(TaskDetailsActivity.this, task.getId());
        } else {
            changeStatusToStartedAndOpenQuestion(false);
        }
    }

    public void continueTaskButtonClick() {
        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case CLAIMED:
            case STARTED:
                startActivity(IntentUtils.getQuestionsIntent(this, task.getId()));
                break;
            case SCHEDULED:
                startActivity(IntentUtils.getTaskValidationIntent(this, task.getId(), false, false));
                break;
            default:
                break;
        }
    }

    public void mapImageViewClick() {
        Bundle bundle = new Bundle();
        bundle.putInt(Keys.MAP_VIEW_ITEM_ID, task.getId());
        bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.SINGLE_TASK.toString());

        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
