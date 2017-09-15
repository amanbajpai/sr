package com.ros.smartrocket.flow.details.task;

import android.app.Dialog;
import android.content.Intent;
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
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.db.entity.ProgressUpdate;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.flow.base.BaseActivity;
import com.ros.smartrocket.flow.details.claim.ClaimMvpPresenter;
import com.ros.smartrocket.flow.details.claim.ClaimMvpView;
import com.ros.smartrocket.flow.details.claim.ClaimPresenter;
import com.ros.smartrocket.flow.map.MapActivity;
import com.ros.smartrocket.flow.media.IdCardActivity;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.ui.dialog.UpdateVersionDialog;
import com.ros.smartrocket.ui.dialog.WithdrawTaskDialog;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.ui.views.OptionsRow;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.NotificationUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.Version;
import com.ros.smartrocket.utils.eventbus.UploadProgressEvent;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class TaskDetailsActivity extends BaseActivity implements ClaimMvpView, TaskDetailsMvpView, View.OnClickListener {
    @BindView(R.id.taskDetailsOptionsRow)
    OptionsRow optionsRow;
    @BindView(R.id.statusText)
    CustomTextView statusText;
    @BindView(R.id.statusTextView)
    CustomTextView statusTextView;
    @BindView(R.id.statusLayout)
    LinearLayout statusLayout;
    @BindView(R.id.startTimeText)
    CustomTextView startTimeText;
    @BindView(R.id.startTimeTextView)
    CustomTextView startTimeTextView;
    @BindView(R.id.startTimeLayout)
    LinearLayout startTimeLayout;
    @BindView(R.id.deadlineTimeText)
    CustomTextView deadlineTimeText;
    @BindView(R.id.deadlineTimeTextView)
    CustomTextView deadlineTimeTextView;
    @BindView(R.id.deadlineTimeLayout)
    LinearLayout deadlineTimeLayout;
    @BindView(R.id.expireText)
    CustomTextView expireText;
    @BindView(R.id.expireTextView)
    CustomTextView expireTextView;
    @BindView(R.id.expireTimeLayout)
    LinearLayout expireTimeLayout;
    @BindView(R.id.statusTimeText)
    CustomTextView statusTimeText;
    @BindView(R.id.statusTimeTextView)
    CustomTextView statusTimeTextView;
    @BindView(R.id.taskIdLayout)
    LinearLayout taskIdLayout;
    @BindView(R.id.taskIdText)
    CustomTextView taskIdText;
    @BindView(R.id.taskIdTextView)
    CustomTextView taskIdTextView;
    @BindView(R.id.statusTimeLayout)
    LinearLayout statusTimeLayout;
    @BindView(R.id.mapImageView)
    ImageView mapImageView;
    @BindView(R.id.timeLayout)
    LinearLayout timeLayout;
    @BindView(R.id.locationName)
    CustomTextView locationName;
    @BindView(R.id.taskAddress)
    CustomTextView taskAddress;
    @BindView(R.id.taskDistance)
    CustomTextView taskDistance;
    @BindView(R.id.taskDescription)
    CustomTextView taskDescription;
    @BindView(R.id.descriptionLayout)
    LinearLayout descriptionLayout;
    @BindView(R.id.withdrawTaskButton)
    CustomButton withdrawTaskButton;
    @BindView(R.id.bookTaskButton)
    CustomButton bookTaskButton;
    @BindView(R.id.startTaskButton)
    CustomButton startTaskButton;
    @BindView(R.id.hideTaskButton)
    CustomButton hideTaskButton;
    @BindView(R.id.showTaskButton)
    CustomButton showTaskButton;
    @BindView(R.id.continueTaskButton)
    CustomButton continueTaskButton;
    @BindView(R.id.redoTaskButton)
    CustomButton redoTaskButton;
    @BindView(R.id.buttonsLayout)
    LinearLayout buttonsLayout;
    @BindView(R.id.previewTaskButton)
    CustomButton previewTaskButton;
    @BindView(R.id.feedbackBtn)
    ImageView feedbackBtn;
    private TextView titleTextView;
    private View idCardView;

    private Integer taskId;
    private Integer missionId;
    private Integer statusId;
    private boolean isPreClaim;
    private Task task;
    private Wave wave = new Wave();
    private ClaimMvpPresenter<ClaimMvpView> claimPresenter;
    private TaskDetailMvpPresenter<TaskDetailsMvpView> taskDetailPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        ButterKnife.bind(this);
        handleArgs();
        initUI();
        initPresenters();
    }

    private void initPresenters() {
        claimPresenter = new ClaimPresenter<>();
        claimPresenter.attachView(this);
        taskDetailPresenter = new TaskDetailPresenter<>();
        taskDetailPresenter.attachView(this);
    }

    private void initUI() {
        setHomeAsUp();
        taskDescription.setMovementMethod(LinkMovementMethod.getInstance());
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        bookTaskButton.setEnabled(false);
        UIUtils.setActionBarBackground(this, statusId, isPreClaim);
    }

    private void handleArgs() {
        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            statusId = getIntent().getIntExtra(Keys.STATUS_ID, 0);
            isPreClaim = getIntent().getBooleanExtra(Keys.IS_PRECLAIM, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        taskDetailPresenter.loadTaskFromDBbyID(taskId, missionId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        switch (networkError.getErrorCode()) {
            case NetworkError.MAXIMUM_MISSION_ERROR_CODE:
                DialogUtils.showMaximumMissionDialog(this);
                break;
            case NetworkError.MAXIMUM_CLAIMS_ERROR_CODE:
                UIUtils.showToastCustomDuration(getString(networkError.getErrorMessageRes()), 8000);
                break;
            default:
                UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
                break;
        }
    }

    @Override
    public void onTaskStarted(Task t) {
        this.task = t;
        setButtonsSettings();
        startActivity(IntentUtils.getQuestionsIntent(TaskDetailsActivity.this, task.getId(), task.getMissionId()));
    }

    public void onTaskStartLater() {
        setButtonsSettings();
        startActivity(IntentUtils.getMainActivityIntent(TaskDetailsActivity.this));
        finish();
    }

    @Override
    public void onTaskUnclaimed() {
        startActivity(IntentUtils.getMainActivityIntent(this));
    }

    @Override
    public void showClaimDialog(String dateTime) {
        new BookTaskSuccessDialog(this, task, dateTime, new BookTaskSuccessDialog
                .DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed(Dialog dialog) {
                claimPresenter.unClaimTaskRequest();
            }

            @Override
            public void onStartLaterButtonPressed(Dialog dialog) {
                TaskDetailsActivity.this.onTaskStartLater();
            }

            @Override
            public void onStartNowButtonPressed(Dialog dialog) {
                claimPresenter.startTask();
            }
        });
    }

    @Override
    public void showUnClaimDialog() {
        String dateTime = UIUtils.longToString(task.getLongExpireDateTime(), 3);
        new WithdrawTaskDialog(this, dateTime, new WithdrawTaskDialog.DialogButtonClickListener() {
            @Override
            public void onNoButtonPressed(Dialog dialog) {
            }

            @Override
            public void onYesButtonPressed(Dialog dialog) {
                claimPresenter.unClaimTask();
            }
        });
    }

    @Override
    public void showDownloadMediaDialog(Wave wave) {
        DialogUtils.showDownloadMediaDialog(this, wave.getMissionSize(),
                new DefaultInfoDialog.DialogButtonClickListener() {
                    @Override
                    public void onLeftButtonPressed(Dialog dialog) {
                        dialog.dismiss();
                        claimPresenter.downloadMedia();
                    }

                    @Override
                    public void onRightButtonPressed(Dialog dialog) {
                        hideLoading();
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onTaskLoadedFromDb(Task loadedTask) {
        task = loadedTask;
        if (task != null) {
            claimPresenter.setTask(task);
            setTaskData();
            taskDetailPresenter.loadWaveFromDB(task.getWaveId());
        }
    }

    @Override
    public void onWaveLoadedFromDb(Wave loadedWave) {
        wave = loadedWave;
        bookTaskButton.setEnabled(!TasksBL.isPreClaimTask(task) || wave.getIsCanBePreClaimed());
        setWaveData();
    }

    @Override
    public void onTasksLoaded() {
        taskDetailPresenter.loadTaskFromDBbyID(taskId, missionId);
    }

    public void setTaskData() {
        startTimeLayout.setVisibility(task.getIsMy() && !TasksBL.isPreClaimTask(task) ? View.GONE : View.VISIBLE);
        deadlineTimeLayout.setVisibility(View.VISIBLE);
        taskIdLayout.setVisibility(View.VISIBLE);
        expireTimeLayout.setVisibility(View.VISIBLE);
        startTimeText.setText(task.getIsMy() ? R.string.available : R.string.start_time);
        deadlineTimeText.setText(task.getIsMy() ? R.string.mission_due : R.string.deadline_time);
        expireText.setText(task.getIsMy() ? R.string.due_in : R.string.duration_time);
        taskIdTextView.setText(String.valueOf(task.getId()));
        optionsRow.setData(task);
        descriptionLayout.setVisibility(TextUtils.isEmpty(task.getDescription()) ? View.GONE : View.VISIBLE);
        taskDescription.setText(TextUtils.isEmpty(task.getDescription()) ? "" : Html.fromHtml(task.getDescription()));

        if (!TextUtils.isEmpty(task.getLocationName()))
            locationName.setText(task.getLocationName());
        else
            locationName.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(task.getAddress())) {
            taskAddress.setText(task.getAddress());
            taskDistance.setText(
                    UIUtils.convertMToKm(this, getDistanceForTask(task), R.string.task_distance_away, false));
        } else {
            taskAddress.setText(R.string.no_mission_address);
            taskDistance.setVisibility(View.GONE);
        }

        startTimeTextView.setText(UIUtils.longToString(task.getLongStartDateTime(), 3));

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
            if (TasksBL.isPreClaimTask(task))
                timeoutInMillisecond = task.getLongPreClaimedTaskExpireAfterStart();
            else
                timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();

            deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
            expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));
        }
        setTaskDataByType();
        setColorTheme();
        setButtonsSettings();
    }

    private float getDistanceForTask(Task task) {
        return TasksBL.getDistanceForTask(task, App.getInstance().getLocationManager().getLocation());
    }

    public void setWaveData() {
        if (titleTextView != null)
            titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));

        if (idCardView != null)
            idCardView.setVisibility(wave.getIdCardStatus() == 1 ? View.VISIBLE : View.GONE);

        UIUtils.showWaveTypeActionBarIcon(this, wave.getIcon());
    }

    public void setTaskDataByType() {
        switch (TasksBL.getTaskStatusType(task.getStatusId())) {
            case COMPLETED:
            case VALIDATION:
                startTimeLayout.setVisibility(View.GONE);
                deadlineTimeLayout.setVisibility(View.GONE);
                expireTimeLayout.setVisibility(View.GONE);
                statusLayout.setVisibility(View.VISIBLE);

                if (Task.TaskStatusId.COMPLETED == TasksBL.getTaskStatusType(task.getStatusId()))
                    statusTextView.setText(getString(R.string.mission_transmitting, getUploadProgress()));
                else
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

    public void setColorTheme() {
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

    public void setButtonsSettings() {
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
                if (UIUtils.isTrue(task.getIsHide()))
                    showTaskButton.setVisibility(View.VISIBLE);
                else
                    hideTaskButton.setVisibility(View.VISIBLE);
                break;
            case CLAIMED:
                buttonsLayout.setVisibility(View.VISIBLE);
                withdrawTaskButton.setVisibility(View.VISIBLE);
                startTaskButton.setVisibility(View.VISIBLE);
                previewTaskButton.setVisibility(View.VISIBLE);
                if (TasksBL.isPreClaimTask(task))
                    startTaskButton.setEnabled(false);
                else
                    startTaskButton.setEnabled(true);
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

    @SuppressWarnings("unused")
    @OnClick(R.id.previewTaskButton)
    public void onPreviewClick() {
        startActivity(IntentUtils.getPreviewQuestionsIntent(this, task.getId(), task.getMissionId()));
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.startTaskButton)
    public void startTask() {
        if (UIUtils.isOnline(this)) claimPresenter.startTask();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.bookTaskButton)
    public void claimTask() {
        final AppVersion appVersion = PreferencesManager.getInstance().getAppVersion();
        Version currentVersion = new Version(BuildConfig.VERSION_NAME);
        Version newestVersion = new Version(appVersion.getLatestVersion());
        if (currentVersion.compareTo(newestVersion) < 0) {
            new UpdateVersionDialog(this, currentVersion.toString(), newestVersion.toString(), new UpdateVersionDialog.DialogButtonClickListener() {
                @Override
                public void onCancelButtonPressed() {
                }

                @Override
                public void onOkButtonPressed() {
                    startActivity(IntentUtils.getBrowserIntent(appVersion.getLatestVersionLink()));
                }
            });
        } else {
            claimPresenter.claimTask();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.withdrawTaskButton)
    public void unClaimTask() {
        claimPresenter.unClaimTask();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.hideTaskButton)
    public void hideTaskButtonClick() {
        task.setIsHide(true);
        setButtonsSettings();
        taskDetailPresenter.setHideTaskOnMapByID(task.getId(), task.getMissionId(), true);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.showTaskButton)
    public void showTaskButtonClick() {
        task.setIsHide(false);
        setButtonsSettings();
        taskDetailPresenter.setHideTaskOnMapByID(task.getId(), task.getMissionId(), false);
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
        if (task != null) showMap();
    }

    private void showMap() {
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
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_task_details);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View actionBarView = actionBar.getCustomView();
            titleTextView = (TextView) actionBarView.findViewById(R.id.titleTextView);
            titleTextView.setText(getString(R.string.task_detail_title, wave.getName()));
            idCardView = actionBarView.findViewById(R.id.idCardButton);
            idCardView.setOnClickListener(view -> {
                LocaleUtils.setCurrentLanguage();
                IdCardActivity.launch(TaskDetailsActivity.this, wave);
            });
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        claimPresenter.detachView();
        taskDetailPresenter.detachView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.feedbackBtn) {
            Intent intent;
            String feedbackShort = task.getFeedBackShort();
            String feedbackFormatted = task.getFeedBackCommentFormatted();
            if (!TextUtils.isEmpty(feedbackShort))
                feedbackShort = "<br><br>" + feedbackShort;
            if (!TextUtils.isEmpty(feedbackFormatted))
                feedbackFormatted = "<br><br>" + feedbackFormatted;

            if (TasksBL.getTaskStatusType(task.getStatusId()) == Task.TaskStatusId.REJECTED)
                intent = NotificationUtils.getRejectedNotificationIntent(this, feedbackShort,
                        feedbackFormatted, task, false);
            else
                intent = NotificationUtils.getApprovedNotificationIntent(this, feedbackShort,
                        feedbackFormatted, task, false);

            startActivity(intent);
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(UploadProgressEvent event) {
        if (taskDetailPresenter != null) {
            if (event.isDone())
                taskDetailPresenter.getMyTasksFromServer();
            else
                taskDetailPresenter.loadTaskFromDBbyID(taskId, missionId);
        }
    }

    private String getUploadProgress() {
        ProgressUpdate progressUpdate = PreferencesManager.getInstance().getUploadProgress();
        if (progressUpdate != null && task != null && task.getId().equals(progressUpdate.getTaskId()))
            return " " + progressUpdate.getUploadedFilesCount() + "/" + progressUpdate.getTotalFilesCount();
        else
            return "";
    }
}
