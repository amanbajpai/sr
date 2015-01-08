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
import com.ros.smartrocket.utils.ClaimTaskManager;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.UIUtils;

import java.util.Locale;

/**
 * Activity for view Task detail information
 */
public class WaveDetailsActivity extends BaseActivity implements View.OnClickListener, ClaimTaskManager.ClaimTaskListener {
    private AsyncQueryHandler handler;
    private ClaimTaskManager claimTaskManager;

    private Integer waveId;
    private Integer statusId;
    private boolean isPreClaim;
    private Wave wave;
    private Task nearTask = new Task();

    private View actionBarView;
    private TextView titleTextView;
    private TextView projectPrice;
    private TextView projectExp;
    private TextView projectLocations;
    private TextView textQuestionsCount;
    private TextView photoQuestionsCount;

    private LinearLayout descriptionLayout;
    private TextView projectDescription;
    private TextView noTaskAddressText;

    private Button claimNearTasksButton;
    private Button hideAllTasksButton;
    private Button showAllTasksButton;

    private View optionDivider;
    private ImageView mapImageView;

    private LinearLayout timeLayout;
    private LinearLayout taskOptionsLayout;
    private LinearLayout buttonsLayout;

    private TextView startTimeTextView;
    private TextView deadlineTimeTextView;
    private TextView expireTextView;

    private TextView startTimeText;
    private TextView deadlineTimeText;
    private TextView expireText;

    private TextView showMissionMapText;

    public WaveDetailsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_wave_details);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            waveId = getIntent().getIntExtra(Keys.WAVE_ID, 0);
            statusId = getIntent().getIntExtra(Keys.STATUS_ID, 0);
            isPreClaim = getIntent().getBooleanExtra(Keys.IS_PRECLAIM, false);
        }

        handler = new DbHandler(getContentResolver());

        timeLayout = (LinearLayout) findViewById(R.id.timeLayout);
        taskOptionsLayout = (LinearLayout) findViewById(R.id.optionsLayout);
        buttonsLayout = (LinearLayout) findViewById(R.id.buttonsLayout);
        optionDivider = findViewById(R.id.optionDivider);

        startTimeText = (TextView) findViewById(R.id.startTimeText);
        deadlineTimeText = (TextView) findViewById(R.id.deadlineTimeText);
        expireText = (TextView) findViewById(R.id.expireText);

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        expireTextView = (TextView) findViewById(R.id.expireTextView);

        showMissionMapText = (TextView) findViewById(R.id.showMissionMapText);

        projectPrice = (TextView) findViewById(R.id.projectPrice);
        projectExp = (TextView) findViewById(R.id.projectExp);
        projectLocations = (TextView) findViewById(R.id.projectLocations);
        textQuestionsCount = (TextView) findViewById(R.id.textQuestionsCount);
        photoQuestionsCount = (TextView) findViewById(R.id.photoQuestionsCount);

        descriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
        projectDescription = (TextView) findViewById(R.id.projectDescription);
        noTaskAddressText = (TextView) findViewById(R.id.noTaskAddressText);

        claimNearTasksButton = (Button) findViewById(R.id.claimNearTasksButton);
        claimNearTasksButton.setOnClickListener(this);
        claimNearTasksButton.setEnabled(false);
        hideAllTasksButton = (Button) findViewById(R.id.hideAllTasksButton);
        hideAllTasksButton.setOnClickListener(this);
        showAllTasksButton = (Button) findViewById(R.id.showAllTasksButton);
        showAllTasksButton.setOnClickListener(this);
        mapImageView = (ImageView) findViewById(R.id.mapImageView);
        mapImageView.setOnClickListener(this);

        UIUtils.setActionBarBackground(this, statusId, isPreClaim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WavesBL.getWaveWithNearTaskFromDB(handler, waveId);
    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case WaveDbSchema.QueryWaveByDistance.TOKEN_QUERY:
                    if (cursor != null && cursor.getCount() > 0) {
                        wave = WavesBL.convertCursorToWaveWithTask(cursor);

                        setWaveData(wave);
                        TasksBL.getTaskFromDBbyID(handler, wave.getNearTaskId());
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
                        claimTaskManager = new ClaimTaskManager(WaveDetailsActivity.this, nearTask, WaveDetailsActivity.this);

                        claimNearTasksButton.setEnabled(!WavesBL.isPreClaimWave(wave) || wave.getIsCanBePreClaimed());


                        setNearTaskData(nearTask);
                    } else {
                        if (cursor != null) {
                            cursor.close();
                        }
                        TasksBL.getTaskFromDBbyID(handler, wave.getNearTaskId());
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

        projectPrice.setText(UIUtils.getBalanceOrPrice(this, wave.getNearTaskPrice(), wave.getNearTaskCurrencySign(),
                null, null));
        projectExp.setText(String.format(Locale.US, "%.0f", wave.getExperienceOffer()));
        projectLocations.setText(String.valueOf(wave.getTaskCount()));
        textQuestionsCount.setText(String.valueOf(wave.getNoPhotoQuestionsCount()));
        photoQuestionsCount.setText(String.valueOf(wave.getPhotoQuestionsCount()));

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

            taskOptionsLayout.setBackgroundColor(violetDarkColorResId);
            optionDivider.setBackgroundColor(violetLightColorResId);
            timeLayout.setBackgroundColor(violetColorResId);
            buttonsLayout.setBackgroundColor(violetDarkColorResId);

            projectPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_violet, 0, 0, 0);
            projectExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_violet, 0, 0, 0);
            projectLocations.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_violet, 0, 0, 0);
            textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_violet, 0, 0, 0);
            photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_violet, 0, 0, 0);

            claimNearTasksButton.setBackgroundResource(R.drawable.button_violet_selector);
            hideAllTasksButton.setBackgroundResource(R.drawable.button_violet_selector);
            showAllTasksButton.setBackgroundResource(R.drawable.button_violet_selector);

            mapImageView.setImageResource(R.drawable.map_piece_violet);
        } else {
            taskOptionsLayout.setBackgroundColor(getResources().getColor(R.color.green_light));
            optionDivider.setBackgroundColor(getResources().getColor(R.color.green_dark));

            projectPrice.setCompoundDrawablesWithIntrinsicBounds(R.drawable.wallet_green, 0, 0, 0);
            projectExp.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rocket_green, 0, 0, 0);
            projectLocations.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location_green, 0, 0, 0);
            textQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quote_green, 0, 0, 0);
            photoQuestionsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.camera_green, 0, 0, 0);
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
        startActivity(IntentUtils.getMainActivityIntent(WaveDetailsActivity.this));
        finish();
    }

    @Override
    public void onStarted(Task task) {
        finish();
        startActivity(IntentUtils.getTaskDetailIntent(this, task.getId(), task.getStatusId(), TasksBL.isPreClaimTask(task)));
        startActivity(IntentUtils.getQuestionsIntent(this, task.getId()));
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

        actionBarView = actionBar.getCustomView();

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
        super.onStop();
    }
}
