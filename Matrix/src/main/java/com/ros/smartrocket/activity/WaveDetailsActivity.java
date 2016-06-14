package com.ros.smartrocket.activity;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.WaveDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.dialog.CustomProgressDialog;
import com.ros.smartrocket.utils.ClaimTaskManager;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.views.CustomButton;
import com.ros.smartrocket.views.CustomTextView;
import com.ros.smartrocket.views.OptionsRow;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Activity for view Task detail information
 */
public class WaveDetailsActivity extends BaseActivity implements
        View.OnClickListener, ClaimTaskManager.ClaimTaskListener {
    @Bind(R.id.startTimeText)
    CustomTextView startTimeText;
    @Bind(R.id.deadlineTimeText)
    CustomTextView deadlineTimeText;
    @Bind(R.id.expireText)
    CustomTextView expireText;
    @Bind(R.id.startTimeTextView)
    CustomTextView startTimeTextView;
    @Bind(R.id.deadlineTimeTextView)
    CustomTextView deadlineTimeTextView;
    @Bind(R.id.expireTextView)
    CustomTextView expireTextView;
    @Bind(R.id.mapImageView)
    ImageView mapImageView;
    @Bind(R.id.showMissionMapText)
    CustomTextView showMissionMapText;
    @Bind(R.id.timeLayout)
    LinearLayout timeLayout;
    @Bind(R.id.waveDetailsOptionsRow)
    OptionsRow optionsRow;
    @Bind(R.id.noTaskAddressText)
    CustomTextView noTaskAddressText;
    @Bind(R.id.projectDescription)
    CustomTextView projectDescription;
    @Bind(R.id.descriptionLayout)
    LinearLayout descriptionLayout;
    @Bind(R.id.claimNearTasksButton)
    CustomButton claimNearTasksButton;
    @Bind(R.id.showAllTasksButton)
    CustomButton showAllTasksButton;
    @Bind(R.id.hideAllTasksButton)
    CustomButton hideAllTasksButton;
    @Bind(R.id.previewTaskButton)
    CustomButton previewTaskButton;
    @Bind(R.id.buttonsLayout)
    LinearLayout buttonsLayout;
    private TextView titleTextView;
    private AsyncQueryHandler handler;
    private ClaimTaskManager claimTaskManager;

    private Integer waveId;
    private Integer statusId;
    private boolean isPreClaim;
    private Wave wave;
    private Task nearTask = new Task();


    private CustomProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_wave_details);
        ButterKnife.bind(this);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            waveId = getIntent().getIntExtra(Keys.WAVE_ID, 0);
            statusId = getIntent().getIntExtra(Keys.STATUS_ID, 0);
            isPreClaim = getIntent().getBooleanExtra(Keys.IS_PRECLAIM, false);
        }

        handler = new DbHandler(getContentResolver());

        timeLayout = (LinearLayout) findViewById(R.id.timeLayout);
        optionsRow = (OptionsRow) findViewById(R.id.waveDetailsOptionsRow);
        buttonsLayout = (LinearLayout) findViewById(R.id.buttonsLayout);
        claimNearTasksButton.setOnClickListener(this);
        claimNearTasksButton.setEnabled(false);
        hideAllTasksButton.setOnClickListener(this);
        showAllTasksButton.setOnClickListener(this);
        previewTaskButton.setOnClickListener(this);
        mapImageView = (ImageView) findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(this);

        UIUtils.setActionBarBackground(this, statusId, isPreClaim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WavesBL.getWaveWithNearTaskFromDB(handler, waveId);
        showProgressBar();
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            dismissProgressBar();
            switch (token) {
                case WaveDbSchema.QueryWaveByDistance.TOKEN_QUERY:
                    if (cursor != null && cursor.getCount() > 0) {

                        wave = WavesBL.convertCursorToWaveWithTask(cursor);

                        setWaveData(wave);
                        TasksBL.getTaskFromDBbyID(handler, wave.getNearTaskId(), 0);
                    } else {
                        if (cursor != null) {
                            cursor.close();
                        }
                        WavesBL.getWaveWithNearTaskFromDB(handler, waveId);
                    }
                    break;
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    if (cursor != null && cursor.getCount() > 0) {
                        nearTask = TasksBL.convertCursorToTask(cursor);
                        if (claimTaskManager!=null){
                            removeNetworkOperationListener(claimTaskManager);
                        }
                        claimTaskManager = new ClaimTaskManager(WaveDetailsActivity.this, nearTask, WaveDetailsActivity.this);

                        claimNearTasksButton.setEnabled(!WavesBL.isPreClaimWave(wave) || wave.getIsCanBePreClaimed());


                        setNearTaskData(nearTask);
                    } else {
                        if (cursor != null) {
                            cursor.close();
                        }
                        TasksBL.getTaskFromDBbyID(handler, wave.getNearTaskId(), 0);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void setWaveData(Wave wave) {
        projectDescription.setText(wave.getDescription());
        descriptionLayout.setVisibility(TextUtils.isEmpty(wave.getDescription()) ? View.GONE : View.VISIBLE);

        long endTimeInMillisecond = UIUtils.isoTimeToLong(wave.getEndDateTime());
        long timeoutInMillisecond;

        if (WavesBL.isPreClaimWave(wave)) {
            timeoutInMillisecond = wave.getLongPreClaimedTaskExpireAfterStart();
        } else {
            timeoutInMillisecond = wave.getLongExpireTimeoutForClaimedTask();
        }

        startTimeTextView.setText(UIUtils.longToString(wave.getLongStartDateTime(), 3));
        deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
        expireTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));
        optionsRow.setData(wave, true);

        UIUtils.showWaveTypeActionBarIcon(this, wave.getIcon());
        setColorTheme(wave);
    }

    public void setNearTaskData(Task task) {
        if (titleTextView != null) {
            titleTextView.setText(getString(R.string.task_detail_title, task.getName()));
        }

        if (!TextUtils.isEmpty(task.getAddress())) {
            noTaskAddressText.setVisibility(View.GONE);
        } else {
            noTaskAddressText.setVisibility(View.VISIBLE);
        }

        setButtonsSettings(task);
    }

    public void setButtonsSettings(Task task) {
        if (wave != null && wave.getTaskCount() == 1 && TextUtils.isEmpty(task.getAddress())) {
            claimNearTasksButton.setVisibility(View.VISIBLE);
            previewTaskButton.setVisibility(View.VISIBLE);
            if (task.getIsHide()) {
                showAllTasksButton.setVisibility(View.VISIBLE);
                hideAllTasksButton.setVisibility(View.GONE);
            } else {
                showAllTasksButton.setVisibility(View.GONE);
                hideAllTasksButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setColorTheme(Wave wave) {
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

    @Override
    public void onClaimed(Task task) {
        // nothing
    }

    @Override
    public void onUnClaimed(Task task) {
        startActivity(IntentUtils.getMainActivityIntent(this));
    }

    @Override
    public void onStartLater(Task task) {
        setButtonsSettings(task);
        startActivity(IntentUtils.getMainActivityIntent(WaveDetailsActivity.this));
        finish();
    }

    @Override
    public void onStarted(Task task) {
        finish();
        startActivity(IntentUtils.getTaskDetailIntent(this, task.getId(), task.getMissionId(), task.getStatusId(),
                TasksBL.isPreClaimTask(task)));
        startActivity(IntentUtils.getQuestionsIntent(this, task.getId(), task.getMissionId()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.claimNearTasksButton:
                claimTaskManager.claimTask();
                break;
            case R.id.hideAllTasksButton:
                if (wave != null && nearTask != null) {
                    nearTask.setIsHide(true);
                    TasksBL.setHideAllProjectTasksOnMapByID(handler, wave.getId(), true);
                    startActivity(IntentUtils.getMainActivityIntent(this));
                    finish();
                }
                break;
            case R.id.showAllTasksButton:
                if (wave != null) {
                    if (nearTask != null) {
                        TasksBL.setHideAllProjectTasksOnMapByID(handler, wave.getId(), false);
                        nearTask.setIsHide(false);

                        setButtonsSettings(nearTask);
                    }

                    startActivity(IntentUtils.getWaveMapIntent(this, wave.getId()));
                }
                break;
            case R.id.mapImageView:
                if (wave != null) {
                    startActivity(IntentUtils.getWaveMapIntent(this, wave.getId()));
                }
                break;
            case R.id.previewTaskButton:
                startActivity(IntentUtils.getPreviewQuestionsIntent(this, nearTask.getId(), nearTask.getMissionId()));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View actionBarView = actionBar.getCustomView();

        if (nearTask != null) {
            titleTextView = (TextView) actionBarView.findViewById(R.id.titleTextView);
            titleTextView.setText(getString(R.string.task_detail_title, nearTask.getName()));
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
    protected void onStop() {
        if (claimTaskManager != null) {
            claimTaskManager.onStop();
        }
        dismissProgressBar();
        super.onStop();
    }

    public void showProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        progressDialog = CustomProgressDialog.show(this);
        progressDialog.setCancelable(false);
    }

    public void dismissProgressBar() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
