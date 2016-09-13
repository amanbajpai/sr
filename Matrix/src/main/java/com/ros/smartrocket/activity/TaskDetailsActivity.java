package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.ProgressUpdate;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.eventbus.UploadProgressEvent;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.net.BaseNetworkService;
import com.ros.smartrocket.net.BaseOperation;
import com.ros.smartrocket.net.NetworkOperationListenerInterface;
import com.ros.smartrocket.utils.ClaimTaskManager;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.MyLog;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomTextView;
import com.ros.smartrocket.views.OptionsRow;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Activity for view Task detail information
 */
public class TaskDetailsActivity extends BaseActivity implements ClaimTaskManager.ClaimTaskListener,
        View.OnClickListener, NetworkOperationListenerInterface {
    @Bind(R.id.taskDetailsOptionsRow)
    OptionsRow optionsRow;
    @Bind(R.id.statusText)
    CustomTextView statusText;
    @Bind(R.id.statusTextView)
    CustomTextView statusTextView;
    @Bind(R.id.statusLayout)
    LinearLayout statusLayout;
    @Bind(R.id.startTimeText)
    CustomTextView startTimeText;
    @Bind(R.id.startTimeTextView)
    CustomTextView startTimeTextView;
    @Bind(R.id.startTimeLayout)
    LinearLayout startTimeLayout;
    @Bind(R.id.deadlineTimeText)
    CustomTextView deadlineTimeText;
    @Bind(R.id.deadlineTimeTextView)
    CustomTextView deadlineTimeTextView;
    @Bind(R.id.deadlineTimeLayout)
    LinearLayout deadlineTimeLayout;
    @Bind(R.id.expireText)
    CustomTextView expireText;
    @Bind(R.id.expireTextView)
    CustomTextView expireTextView;
    @Bind(R.id.expireTimeLayout)
    LinearLayout expireTimeLayout;
    @Bind(R.id.statusTimeText)
    CustomTextView statusTimeText;
    @Bind(R.id.statusTimeTextView)
    CustomTextView statusTimeTextView;
    @Bind(R.id.taskIdLayout)
    LinearLayout taskIdLayout;
    @Bind(R.id.taskIdText)
    CustomTextView taskIdText;
    @Bind(R.id.taskIdTextView)
    CustomTextView taskIdTextView;
    @Bind(R.id.statusTimeLayout)
    LinearLayout statusTimeLayout;
    @Bind(R.id.mapImageView)
    ImageView mapImageView;
    @Bind(R.id.timeLayout)
    LinearLayout timeLayout;
    @Bind(R.id.locationName)
    CustomTextView locationName;
    @Bind(R.id.taskAddress)
    CustomTextView taskAddress;
    @Bind(R.id.taskDistance)
    CustomTextView taskDistance;
    @Bind(R.id.taskDescription)
    CustomTextView taskDescription;
    @Bind(R.id.descriptionLayout)
    LinearLayout descriptionLayout;
    @Bind(R.id.withdrawTaskButton)
    CustomButton withdrawTaskButton;
    @Bind(R.id.bookTaskButton)
    CustomButton bookTaskButton;
    @Bind(R.id.startTaskButton)
    CustomButton startTaskButton;
    @Bind(R.id.hideTaskButton)
    CustomButton hideTaskButton;
    @Bind(R.id.showTaskButton)
    CustomButton showTaskButton;
    @Bind(R.id.continueTaskButton)
    CustomButton continueTaskButton;
    @Bind(R.id.redoTaskButton)
    CustomButton redoTaskButton;
    @Bind(R.id.buttonsLayout)
    LinearLayout buttonsLayout;
    @Bind(R.id.previewTaskButton)
    CustomButton previewTaskButton;
    @Bind(R.id.feedbackBtn)
    ImageView feedbackBtn;
    private TextView titleTextView;
    private View idCardView;

    private AsyncQueryHandler handler;
    private ClaimTaskManager claimTaskManager;

    private Integer taskId;
    private Integer missionId;
    private Integer statusId;
    private boolean isPreClaim;
    private Task task;
    private Wave wave = new Wave();


    public TaskDetailsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);

        taskDescription.setMovementMethod(LinkMovementMethod.getInstance());
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            statusId = getIntent().getIntExtra(Keys.STATUS_ID, 0);
            isPreClaim = getIntent().getBooleanExtra(Keys.IS_PRECLAIM, false);
        }
        handler = new DbHandler(getContentResolver());
        bookTaskButton.setEnabled(false);
        UIUtils.setActionBarBackground(this, statusId, isPreClaim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        TasksBL.getTaskFromDBbyID(handler, taskId, missionId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onNetworkOperation(BaseOperation operation) {
        if (Keys.GET_MY_TASKS_OPERATION_TAG.equals(operation.getTag())) {
            if (operation.getResponseStatusCode() == BaseNetworkService.SUCCESS) {
                if (handler != null && taskId != null && missionId != null) {
                    TasksBL.getTaskFromDBbyID(handler, taskId, missionId);
                }
            }
        }
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
                        if (claimTaskManager != null) {
                            removeNetworkOperationListener(claimTaskManager);
                        }
                        claimTaskManager = new ClaimTaskManager(TaskDetailsActivity.this, task, TaskDetailsActivity
                                .this);

                        setTaskData(task);
                        WavesBL.getWaveFromDB(handler, task.getWaveId());
                    } else {
                        if (cursor != null) {
                            cursor.close();
                        }
                        finish();
                    }
                    break;
                case WaveDbSchema.Query.TOKEN_QUERY:
                    wave = WavesBL.convertCursorToWave(cursor);
                    bookTaskButton.setEnabled(!TasksBL.isPreClaimTask(task) || wave.getIsCanBePreClaimed());
                    setWaveData(wave);
                    break;
                default:
                    break;
            }
        }
    }

    public void setTaskData(Task task) {
        startTimeLayout.setVisibility(task.getIsMy() && !TasksBL.isPreClaimTask(task) ? View.GONE : View.VISIBLE);
        deadlineTimeLayout.setVisibility(View.VISIBLE);
        taskIdLayout.setVisibility(View.VISIBLE);
        expireTimeLayout.setVisibility(View.VISIBLE);

        int missionDueResId;
        int dueInResId;

        missionDueResId = R.string.mission_due;
        dueInResId = R.string.due_in;

        startTimeText.setText(task.getIsMy() ? R.string.available : R.string.start_time);
        deadlineTimeText.setText(task.getIsMy() ? missionDueResId : R.string.deadline_time);
        expireText.setText(task.getIsMy() ? dueInResId : R.string.duration_time);
        taskIdTextView.setText(String.valueOf(task.getId()));
        optionsRow.setData(task);
        descriptionLayout.setVisibility(TextUtils.isEmpty(task.getDescription()) ? View.GONE : View.VISIBLE);
        taskDescription.setText(TextUtils.isEmpty(task.getDescription()) ? "" : Html.fromHtml(task.getDescription()));


        if (!TextUtils.isEmpty(task.getLocationName())) {
            locationName.setText(task.getLocationName());
        } else {
            locationName.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(task.getAddress())) {
            taskAddress.setText(task.getAddress());
            taskDistance.setText(UIUtils.convertMToKm(this, getDistanceForTask(task), R.string.task_distance_away,
                    false));
        } else {
            taskAddress.setText(R.string.no_mission_address);
            taskDistance.setVisibility(View.GONE);
        }

        long startTimeInMillisecond = task.getLongStartDateTime();
        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));

        if (task.getIsMy()) {
            long expireTimeInMillisecond = task.getLongExpireDateTime();
            if (expireTimeInMillisecond != 0) {
                long dueInMillisecond = expireTimeInMillisecond - Calendar.getInstance().getTimeInMillis();

                deadlineTimeTextView.setText(UIUtils.longToString(expireTimeInMillisecond, 3));
                expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, dueInMillisecond));
            } else {
                deadlineTimeTextView.setVisibility(View.INVISIBLE);
                expireTextView.setVisibility(View.INVISIBLE);
            }
        } else {
            long endTimeInMillisecond = task.getLongEndDateTime();
            long timeoutInMillisecond;

            if (TasksBL.isPreClaimTask(task)) {
                timeoutInMillisecond = task.getLongPreClaimedTaskExpireAfterStart();
            } else {
                timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();
            }

            deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
            expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));
        }

        setTaskDataByType(task);
        setColorTheme(task);
        setButtonsSettings(task);
    }

    private float getDistanceForTask(Task task) {
        return TasksBL.getDistanceForTask(task, App.getInstance().getLocationManager().getLocation());
    }

    public void setWaveData(Wave wave) {
        MyLog.v("TaskDetailsActivity.setWaveData", wave.getIdCardStatus());
        if (titleTextView != null) {
            titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));
        }
        if (idCardView != null) {
            idCardView.setVisibility(wave.getIdCardStatus() == 1 ? View.VISIBLE : View.GONE);
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

                if (Task.TaskStatusId.COMPLETED == TasksBL.getTaskStatusType(task.getStatusId())) {
                    statusTextView.setText(getString(R.string.mission_transmitting, getUploadProgress()));
                } else {
                    statusTextView.setText(getString(R.string.in_validation_task));
                }

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
            case RE_DO_TASK:
                long expireTimeInMillisecond = task.getLongExpireDateTime();
                if (expireTimeInMillisecond != 0) {
                    long dueInMillisecond = expireTimeInMillisecond - Calendar.getInstance().getTimeInMillis();

                    deadlineTimeTextView.setText(UIUtils.longToString(expireTimeInMillisecond, 3));
                    expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, dueInMillisecond));
                } else {
                    deadlineTimeTextView.setVisibility(View.INVISIBLE);
                    expireTextView.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    public void setColorTheme(Task task) {
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
                    taskIdText.setTextColor(violetLightColorResId);

                    statusTextView.setTextColor(whiteColorResId);
                    taskIdTextView.setTextColor(whiteColorResId);
                    startTimeTextView.setTextColor(whiteColorResId);
                    deadlineTimeTextView.setTextColor(whiteColorResId);
                    expireTextView.setTextColor(whiteColorResId);
                    statusTimeTextView.setTextColor(whiteColorResId);

                    timeLayout.setBackgroundColor(violetColorResId);
                    buttonsLayout.setBackgroundColor(violetDarkColorResId);

                    bookTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    startTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    hideTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    showTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    withdrawTaskButton.setBackgroundResource(R.drawable.button_violet_selector);
                    continueTaskButton.setBackgroundResource(R.drawable.button_violet_selector);

                    mapImageView.setImageResource(R.drawable.map_piece_violet);
                }
                break;
            case COMPLETED:
                mapImageView.setImageResource(R.drawable.map_piece_grey);
                break;
            case VALIDATION:
                mapImageView.setImageResource(R.drawable.map_piece_grey);
                break;
            case RE_DO_TASK:
                buttonsLayout.setBackgroundColor(getResources().getColor(R.color.red_dark));
                mapImageView.setImageResource(R.drawable.map_piece_red);
                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
                mapImageView.setImageResource(R.drawable.map_piece_yellow);
                feedbackBtn.setImageResource(R.drawable.btn_feedback_orange);
                break;
            case REJECTED:
                mapImageView.setImageResource(R.drawable.map_piece_black);
                feedbackBtn.setImageResource(R.drawable.btn_feedback_graphite);
                break;
            default:
                break;
        }
    }

    public void setButtonsSettings(Task task) {
        buttonsLayout.setVisibility(View.GONE);
        bookTaskButton.setVisibility(View.GONE);
        startTaskButton.setVisibility(View.GONE);
        previewTaskButton.setVisibility(View.GONE);
        hideTaskButton.setVisibility(View.GONE);
        showTaskButton.setVisibility(View.GONE);
        withdrawTaskButton.setVisibility(View.GONE);
        continueTaskButton.setVisibility(View.GONE);
        redoTaskButton.setVisibility(View.GONE);
        feedbackBtn.setVisibility(View.INVISIBLE);

        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case NONE:
                buttonsLayout.setVisibility(View.VISIBLE);
                bookTaskButton.setVisibility(View.VISIBLE);
                previewTaskButton.setVisibility(View.VISIBLE);
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
                previewTaskButton.setVisibility(View.VISIBLE);
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
            case RE_DO_TASK:
                buttonsLayout.setVisibility(View.VISIBLE);
                withdrawTaskButton.setVisibility(View.VISIBLE);
                withdrawTaskButton.setBackgroundResource(R.drawable.button_red_selector);
                redoTaskButton.setVisibility(View.VISIBLE);
                break;
            case VALIDATED:
            case IN_PAYMENT_PROCESS:
            case PAID:
            case REJECTED:
                feedbackBtn.setVisibility(View.VISIBLE);
                feedbackBtn.setOnClickListener(this);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClaimed(Task task) {

    }

    @Override
    public void onUnClaimed(Task task) {
        startActivity(IntentUtils.getMainActivityIntent(this));
    }

    @Override
    public void onStartLater(Task task) {
        setButtonsSettings(task);
        startActivity(IntentUtils.getMainActivityIntent(TaskDetailsActivity.this));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        addNetworkOperationListener(this);
    }

    @Override
    public void onStarted(Task task) {
        setButtonsSettings(task);
        startActivity(IntentUtils.getQuestionsIntent(TaskDetailsActivity.this, task.getId(), task.getMissionId()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.previewTaskButton)
    public void onPreviewClick() {
        startActivity(IntentUtils.getPreviewQuestionsIntent(this, task.getId(), task.getMissionId()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.startTaskButton)
    public void startTask() {
        claimTaskManager.startTask();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bookTaskButton)
    public void claimTask() {
        claimTaskManager.claimTask();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.withdrawTaskButton)
    public void unClaimTask() {
        claimTaskManager.unClaimTask();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.hideTaskButton)
    public void hideTaskButtonClick() {
        task.setIsHide(true);
        setButtonsSettings(task);
        TasksBL.setHideTaskOnMapByID(handler, task.getId(), task.getMissionId(), true);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.showTaskButton)
    public void showTaskButtonClick() {
        task.setIsHide(false);
        setButtonsSettings(task);
        TasksBL.setHideTaskOnMapByID(handler, task.getId(), task.getMissionId(), false);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.continueTaskButton)
    public void continueTaskButtonClick() {
        if (task != null) {
            switch (TasksBL.getTaskStatusType(task.getStatusId())) {
                case CLAIMED:
                case STARTED:
                    startActivity(IntentUtils.getQuestionsIntent(this, taskId, missionId));
                    break;
                case SCHEDULED:
                    startActivity(IntentUtils.getTaskValidationIntent(this, taskId, missionId, false, false));
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.redoTaskButton)
    public void redoTaskButtonClick() {
        startActivity(IntentUtils.getQuestionsIntent(this, task.getId(), task.getMissionId()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.mapImageView)
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
        actionBar.setCustomView(R.layout.actionbar_custom_view_task_details);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View actionBarView = actionBar.getCustomView();

        titleTextView = (TextView) actionBarView.findViewById(R.id.titleTextView);
        titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));
        idCardView = actionBarView.findViewById(R.id.idCardButton);
        idCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdCardActivity.launch(TaskDetailsActivity.this, wave);
            }
        });
        return true;
    }

    @Override
    protected void onStop() {
        if (claimTaskManager != null) {
            claimTaskManager.onStop();
        }
        removeNetworkOperationListener(this);
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.feedbackBtn) {
            Intent intent;
            String feedbackShort = task.getFeedBackShort();
            String feedbackFormatted = task.getFeedBackCommentFormatted();
            if (!TextUtils.isEmpty(feedbackShort)) {
                feedbackShort = "<br><br>" + feedbackShort;
            }
            if (!TextUtils.isEmpty(feedbackFormatted)) {
                feedbackFormatted = "<br><br>" + feedbackFormatted;
            }
            if (TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.REJECTED) {
                intent = NotificationUtils.getRejectedNotificationIntent(this, feedbackShort,
                        feedbackFormatted, task, false);
            } else {
                intent = NotificationUtils.getApprovedNotificationIntent(this, feedbackShort,
                        feedbackFormatted, task, false);
            }
            startActivity(intent);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(UploadProgressEvent event) {
        if (handler != null && taskId != null && missionId != null) {
            if (event.isDone()) {
                sendNetworkOperation(APIFacade.getInstance().getMyTasksOperation());
            } else {
                TasksBL.getTaskFromDBbyID(handler, taskId, missionId);
            }
        }
    }

    private String getUploadProgress() {
        ProgressUpdate progressUpdate = PreferencesManager.getInstance().getUploadProgress();
        if (progressUpdate != null && task != null && task.getId().equals(progressUpdate.getTaskId())) {
            StringBuilder sb = new StringBuilder(" ");
            sb.append(progressUpdate.getUploadedFilesCount());
            sb.append("/");
            sb.append(progressUpdate.getTotalFilesCount());
            return sb.toString();
        } else {
            return "";
        }
    }
}
