package com.ros.smartrocket.presentation.details.wave;

import android.app.Dialog;
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

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.details.claim.ClaimMvpPresenter;
import com.ros.smartrocket.presentation.details.claim.ClaimMvpView;
import com.ros.smartrocket.presentation.details.claim.ClaimPresenter;
import com.ros.smartrocket.ui.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.ui.dialog.WithdrawTaskDialog;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.ui.views.OptionsRow;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WaveDetailsActivity extends BaseActivity implements ClaimMvpView, WaveDetailsMvpView {
    @BindView(R.id.startTimeText)
    CustomTextView startTimeText;
    @BindView(R.id.deadlineTimeText)
    CustomTextView deadlineTimeText;
    @BindView(R.id.expireText)
    CustomTextView expireText;
    @BindView(R.id.startTimeTextView)
    CustomTextView startTimeTextView;
    @BindView(R.id.deadlineTimeTextView)
    CustomTextView deadlineTimeTextView;
    @BindView(R.id.expireTextView)
    CustomTextView expireTextView;
    @BindView(R.id.mapImageView)
    ImageView mapImageView;
    @BindView(R.id.showMissionMapText)
    CustomTextView showMissionMapText;
    @BindView(R.id.timeLayout)
    LinearLayout timeLayout;
    @BindView(R.id.waveDetailsOptionsRow)
    OptionsRow optionsRow;
    @BindView(R.id.noTaskAddressText)
    CustomTextView noTaskAddressText;
    @BindView(R.id.projectDescription)
    CustomTextView projectDescription;
    @BindView(R.id.descriptionLayout)
    LinearLayout descriptionLayout;
    @BindView(R.id.claimNearTasksButton)
    CustomButton claimNearTasksButton;
    @BindView(R.id.showAllTasksButton)
    CustomButton showAllTasksButton;
    @BindView(R.id.hideAllTasksButton)
    CustomButton hideAllTasksButton;
    @BindView(R.id.previewTaskButton)
    CustomButton previewTaskButton;
    @BindView(R.id.buttonsLayout)
    LinearLayout buttonsLayout;

    private TextView titleTextView;
    private Integer waveId;
    private Integer statusId;
    private boolean isPreClaim;
    private Wave wave;
    private Task nearTask = new Task();
    private WaveDetailsMvpPresenter<WaveDetailsMvpView> waveDetailsPresenter;
    private ClaimMvpPresenter<ClaimMvpView> claimPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_details);
        ButterKnife.bind(this);
        handleArgs();
        initUI();
        initPresenters();
    }

    private void initPresenters() {
        claimPresenter = new ClaimPresenter<>();
        claimPresenter.attachView(this);
        waveDetailsPresenter = new WaveDetailsPresenter<>();
        waveDetailsPresenter.attachView(this);
    }

    private void handleArgs() {
        if (getIntent() != null) {
            waveId = getIntent().getIntExtra(Keys.WAVE_ID, 0);
            statusId = getIntent().getIntExtra(Keys.STATUS_ID, 0);
            isPreClaim = getIntent().getBooleanExtra(Keys.IS_PRECLAIM, false);
        }
    }

    private void initUI() {
        setHomeAsUp();
        projectDescription.setMovementMethod(LinkMovementMethod.getInstance());
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        claimNearTasksButton.setEnabled(false);
        UIUtils.setActionBarBackground(this, statusId, isPreClaim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        waveDetailsPresenter.loadWaveWithNearTaskFromDB(waveId);
    }

    @Override
    public void onTaskStarted(Task task) {
        startActivity(IntentUtils.getTaskDetailIntent(this, task.getId(), task.getMissionId(), task.getStatusId(),
                TasksBL.isPreClaimTask(task)));
        startActivity(IntentUtils.getQuestionsIntent(this, task.getId(), task.getMissionId()));
        finish();
    }

    @Override
    public void onTaskUnclaimed() {
        startActivity(IntentUtils.getMainActivityIntent(this));
    }

    @Override
    public void showClaimDialog(String date) {
        new BookTaskSuccessDialog(this, nearTask, date, new BookTaskSuccessDialog
                .DialogButtonClickListener() {
            @Override
            public void onCancelButtonPressed(Dialog dialog) {
                claimPresenter.unClaimTaskRequest();
            }

            @Override
            public void onStartLaterButtonPressed(Dialog dialog) {
                WaveDetailsActivity.this.onTaskStartLater();
            }

            @Override
            public void onStartNowButtonPressed(Dialog dialog) {
                claimPresenter.startTask();
            }
        });
    }

    @Override
    public void showUnClaimDialog() {
        String dateTime = UIUtils.longToString(nearTask.getLongExpireDateTime(), 3);
        new WithdrawTaskDialog(this, dateTime, new WithdrawTaskDialog.DialogButtonClickListener() {
            @Override
            public void onNoButtonPressed(Dialog dialog) {
            }

            @Override
            public void onYesButtonPressed(Dialog dialog) {
                claimPresenter.unClaimTaskRequest();
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

    @OnClick({R.id.claimNearTasksButton, R.id.previewTaskButton, R.id.showAllTasksButton, R.id.hideAllTasksButton, R.id.mapImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.claimNearTasksButton:
                if (nearTask != null) {
                    claimPresenter.setTask(nearTask);
                    claimPresenter.claimTask();
                }
                break;
            case R.id.previewTaskButton:
                startActivity(IntentUtils.getPreviewQuestionsIntent(this, nearTask.getId(), nearTask.getMissionId()));
                break;
            case R.id.showAllTasksButton:
                if (wave != null && nearTask != null)
                    waveDetailsPresenter.setHideAllProjectTasksOnMapByID(wave.getId(), false);
                break;
            case R.id.hideAllTasksButton:
                if (wave != null && nearTask != null)
                    waveDetailsPresenter.setHideAllProjectTasksOnMapByID(wave.getId(), true);
                break;
            case R.id.mapImageView:
                if (wave != null) startActivity(IntentUtils.getWaveMapIntent(this, wave.getId()));
                break;
        }
    }

    @Override
    public void onTasksHided() {
        startActivity(IntentUtils.getMainActivityIntent(this));
        finish();
    }

    @Override
    public void onTasksUnHided() {
        nearTask.setIsHide(false);
        setButtonsSettings();
        startActivity(IntentUtils.getWaveMapIntent(this, wave.getId()));
    }

    @Override
    public void onWaveLoadedFromDb(Wave w) {
        wave = w;
        setWaveData();
        waveDetailsPresenter.loadTaskFromDBbyID(wave.getNearTaskId(), 0);
    }

    @Override
    public void onNearTaskLoadedFromDb(Task task) {
        nearTask = task;
        claimPresenter.setTask(nearTask);
        claimNearTasksButton.setEnabled(!WavesBL.isPreClaimWave(wave) || wave.getIsCanBePreClaimed());
        setNearTaskData();
    }

    public void setWaveData() {
        if (wave != null) {
            projectDescription.setText(TextUtils.isEmpty(wave.getDescription()) ? "" : Html.fromHtml(wave.getDescription()));
            descriptionLayout.setVisibility(TextUtils.isEmpty(wave.getDescription()) ? View.GONE : View.VISIBLE);
            long endTimeInMillisecond = UIUtils.isoTimeToLong(wave.getEndDateTime());
            long timeoutInMillisecond;
            if (WavesBL.isPreClaimWave(wave))
                timeoutInMillisecond = wave.getLongPreClaimedTaskExpireAfterStart();
            else
                timeoutInMillisecond = wave.getLongExpireTimeoutForClaimedTask();
            startTimeTextView.setText(UIUtils.longToString(wave.getLongStartDateTime(), 3));
            deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
            expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));
            optionsRow.setData(wave, true);
            UIUtils.showWaveTypeActionBarIcon(this, wave.getIcon());
            setColorTheme();
        }
    }

    public void setNearTaskData() {
        if (titleTextView != null)
            titleTextView.setText(getString(R.string.task_detail_title, nearTask.getName()));

        if (!TextUtils.isEmpty(nearTask.getAddress()))
            noTaskAddressText.setVisibility(View.GONE);
        else
            noTaskAddressText.setVisibility(View.VISIBLE);
        setButtonsSettings();
    }

    public void setButtonsSettings() {
        if (wave != null && wave.getTaskCount() == 1 && TextUtils.isEmpty(nearTask.getAddress())) {
            claimNearTasksButton.setVisibility(View.VISIBLE);
            previewTaskButton.setVisibility(View.VISIBLE);
            if (nearTask.getIsHide()) {
                showAllTasksButton.setVisibility(View.VISIBLE);
                hideAllTasksButton.setVisibility(View.GONE);
            } else {
                showAllTasksButton.setVisibility(View.GONE);
                hideAllTasksButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setColorTheme() {
        if (WavesBL.isPreClaimWave(wave)) {
            int violetLightColorResId = getResources().getColor(R.color.violet_light);
            int violetDarkColorResId = getResources().getColor(R.color.violet_dark);
            int violetColorResId = getResources().getColor(R.color.violet);
            int whiteColorResId = getResources().getColor(R.color.white);

            startTimeText.setTextColor(violetLightColorResId);
            deadlineTimeText.setTextColor(violetLightColorResId);
            expireText.setTextColor(violetLightColorResId);
            startTimeTextView.setTextColor(whiteColorResId);
            deadlineTimeTextView.setTextColor(whiteColorResId);
            expireTextView.setTextColor(whiteColorResId);
            showMissionMapText.setTextColor(violetLightColorResId);
            timeLayout.setBackgroundColor(violetColorResId);
            buttonsLayout.setBackgroundColor(violetDarkColorResId);
            claimNearTasksButton.setBackgroundResource(R.drawable.button_violet_selector);
            hideAllTasksButton.setBackgroundResource(R.drawable.button_violet_selector);
            showAllTasksButton.setBackgroundResource(R.drawable.button_violet_selector);
            mapImageView.setImageResource(R.drawable.map_piece_violet);
        }
    }

    public void onTaskStartLater() {
        setButtonsSettings();
        startActivity(IntentUtils.getMainActivityIntent(WaveDetailsActivity.this));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            View actionBarView = actionBar.getCustomView();
            if (nearTask != null) {
                titleTextView = (TextView) actionBarView.findViewById(R.id.titleTextView);
                titleTextView.setText(getString(R.string.task_detail_title, nearTask.getName()));
            }
        }
        return true;
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
    protected void onDestroy() {
        super.onDestroy();
        hideLoading();
        claimPresenter.detachView();
        waveDetailsPresenter.detachView();
    }

    @Override
    public void showNetworkError(BaseNetworkError networkError) {

    }

}
