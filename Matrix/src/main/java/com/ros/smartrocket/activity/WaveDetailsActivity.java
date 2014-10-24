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

import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.bl.AnswersBL;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.dialog.BookTaskSuccessDialog;
import com.ros.smartrocket.dialog.CustomProgressDialog;
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
public class WaveDetailsActivity extends BaseActivity implements View.OnClickListener, NetworkOperationListenerInterface {
    private APIFacade apiFacade = APIFacade.getInstance();
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private Calendar calendar = Calendar.getInstance();
    private AsyncQueryHandler handler;

    private Task nearTask = new Task();
    private Wave wave = new Wave();

    private TextView startTimeTextView;
    private TextView deadlineTimeTextView;
    private TextView dueTextView;

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

    private CustomProgressDialog progressDialog;

    public WaveDetailsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_wave_details);

        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));

        if (getIntent() != null) {
            wave = (Wave) getIntent().getSerializableExtra(Keys.WAVE);
        }

        progressDialog = CustomProgressDialog.show(this);
        progressDialog.setCancelable(false);
        progressDialog.hide();

        handler = new DbHandler(getContentResolver());

        startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
        deadlineTimeTextView = (TextView) findViewById(R.id.deadlineTimeTextView);
        dueTextView = (TextView) findViewById(R.id.expireTextView);

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
        hideAllTasksButton = (Button) findViewById(R.id.hideAllTasksButton);
        hideAllTasksButton.setOnClickListener(this);
        showAllTasksButton = (Button) findViewById(R.id.showAllTasksButton);
        showAllTasksButton.setOnClickListener(this);
        findViewById(R.id.mapImageView).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setWaveData(wave);

        TasksBL.getTaskFromDBbyID(handler, wave.getNearTaskId());

    }

    class DbHandler extends AsyncQueryHandler {
        public DbHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
                case TaskDbSchema.Query.All.TOKEN_QUERY:
                    nearTask = TasksBL.convertCursorToTask(cursor);

                    setNearTaskData(nearTask);
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
                        apiFacade.claimTask(WaveDetailsActivity.this, nearTask.getId(), location.getLatitude(), location.getLongitude());
                        setSupportProgressBarIndeterminateVisibility(false);
                    }
                });
            } else if (Keys.CLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.hide();

                long startTimeInMillisecond = nearTask.getLongStartDateTime();
                long preClaimedExpireInMillisecond = nearTask.getLongPreClaimedTaskExpireAfterStart();
                long claimTimeInMillisecond = calendar.getTimeInMillis();
                long timeoutInMillisecond = nearTask.getLongExpireTimeoutForClaimedTask();

                long missionDueMillisecond;
                if (TasksBL.isPreClaimTask(nearTask)) {
                    missionDueMillisecond = startTimeInMillisecond + preClaimedExpireInMillisecond;
                } else {
                    missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
                }

                nearTask.setStatusId(Task.TaskStatusId.CLAIMED.getStatusId());
                nearTask.setIsMy(true);
                nearTask.setClaimed(UIUtils.longToString(claimTimeInMillisecond, 2));
                nearTask.setLongClaimDateTime(claimTimeInMillisecond);

                TasksBL.updateTask(handler, nearTask);

                String dateTime = UIUtils.longToString(missionDueMillisecond, 3);

                new BookTaskSuccessDialog(this, nearTask, dateTime, new BookTaskSuccessDialog.DialogButtonClickListener() {
                    @Override
                    public void onCancelButtonPressed(Dialog dialog) {
                        progressDialog.show();
                        apiFacade.unclaimTask(WaveDetailsActivity.this, nearTask.getId());
                    }

                    @Override
                    public void onStartLaterButtonPressed(Dialog dialog) {
                        setButtonsSettings(nearTask);
                        startActivity(IntentUtils.getMainActivityIntent(WaveDetailsActivity.this));
                        finish();
                    }

                    @Override
                    public void onStartNowButtonPressed(Dialog dialog) {
                        progressDialog.show();
                        setButtonsSettings(nearTask);
                        apiFacade.startTask(WaveDetailsActivity.this, nearTask.getId());

                    }
                });

            } else if (Keys.UNCLAIM_TASK_OPERATION_TAG.equals(operation.getTag())) {
                progressDialog.hide();

                preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + nearTask.getWaveId() + "_"
                        + nearTask.getId());

                nearTask.setStatusId(Task.TaskStatusId.NONE.getStatusId());
                nearTask.setStarted("");
                nearTask.setIsMy(false);
                setButtonsSettings(nearTask);
                TasksBL.updateTask(handler, nearTask);

                QuestionsBL.removeQuestionsFromDB(this, wave.getId(), nearTask.getId());
                AnswersBL.removeAnswersByTaskId(this, nearTask.getId());

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

    public void setWaveData(Wave wave) {
        projectDescription.setText(wave.getDescription());
        descriptionLayout.setVisibility(TextUtils.isEmpty(wave.getDescription()) ? View.GONE : View.VISIBLE);

        long startTimeInMillisecond = UIUtils.isoTimeToLong(wave.getStartDateTime());
        long endTimeInMillisecond = UIUtils.isoTimeToLong(wave.getEndDateTime());
        long timeoutInMillisecond = wave.getLongExpireTimeoutForClaimedTask();

        startTimeTextView.setText(UIUtils.longToString(startTimeInMillisecond, 3));
        deadlineTimeTextView.setText(UIUtils.longToString(endTimeInMillisecond, 3));
        dueTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, timeoutInMillisecond));

        projectPrice.setText(UIUtils.getBalanceOrPrice(this, wave.getNearTaskPrice(), wave.getNearTaskCurrencySign(),
                null, null));
        projectExp.setText(String.format(Locale.US, "%.0f", wave.getExperienceOffer()));
        projectLocations.setText(String.valueOf(wave.getTaskCount()));
        textQuestionsCount.setText(String.valueOf(wave.getNoPhotoQuestionsCount()));
        photoQuestionsCount.setText(String.valueOf(wave.getPhotoQuestionsCount()));

        UIUtils.showWaveTypeActionBarIcon(this, wave.getIcon());
    }

    public void setNearTaskData(Task task) {
        if (!TextUtils.isEmpty(task.getAddress())) {
            noTaskAddressText.setVisibility(View.GONE);
        } else {
            noTaskAddressText.setVisibility(View.VISIBLE);
        }

        setButtonsSettings(task);
    }

    public void setButtonsSettings(Task task) {
        if (wave.getTaskCount() == 1 && TextUtils.isEmpty(task.getAddress())) {
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

    public void changeStatusToStartedAndOpenQuestion(boolean startedStatusSent) {
        nearTask.setStatusId(Task.TaskStatusId.STARTED.getStatusId());
        nearTask.setStarted(UIUtils.longToString(calendar.getTimeInMillis(), 2));
        nearTask.setStartedStatusSent(startedStatusSent);

        TasksBL.updateTask(handler, nearTask);
        finish();
        startActivity(IntentUtils.getTaskDetailIntent(this, nearTask.getId()));
        startActivity(IntentUtils.getQuestionsIntent(this, nearTask.getId()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.claimNearTasksButton:
                claimNearTasksButtonClick();
                break;
            case R.id.hideAllTasksButton:
                if (nearTask != null) {
                    nearTask.setIsHide(true);
                    TasksBL.setHideAllProjectTasksOnMapByID(handler, wave.getId(), true);
                    startActivity(IntentUtils.getMainActivityIntent(this));
                    finish();
                }
                break;
            case R.id.showAllTasksButton:
                if (nearTask != null) {
                    TasksBL.setHideAllProjectTasksOnMapByID(handler, wave.getId(), false);
                    nearTask.setIsHide(false);

                    setButtonsSettings(nearTask);
                }
            case R.id.mapImageView:
                Bundle bundle = new Bundle();
                bundle.putInt(Keys.MAP_VIEW_ITEM_ID, wave.getId());
                bundle.putString(Keys.MAP_MODE_VIEWTYPE, Keys.MapViewMode.WAVE_TASKS.toString());
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void claimNearTasksButtonClick() {
        progressDialog.show();
        apiFacade.getQuestions(this, nearTask.getWaveId(), nearTask.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        View view = actionBar.getCustomView();
        ((TextView) view.findViewById(R.id.titleTextView)).setText(wave.getName());

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
