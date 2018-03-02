package com.ros.smartrocket.presentation.validation;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.interfaces.BaseNetworkError;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.net.UploadFileService;
import com.ros.smartrocket.presentation.base.BaseActivity;
import com.ros.smartrocket.presentation.validation.local.ValidationLocalMvpPresenter;
import com.ros.smartrocket.presentation.validation.local.ValidationLocalMvpView;
import com.ros.smartrocket.presentation.validation.local.ValidationLocalPresenter;
import com.ros.smartrocket.presentation.validation.net.ValidationNetMvpPresenter;
import com.ros.smartrocket.presentation.validation.net.ValidationNetMvpView;
import com.ros.smartrocket.presentation.validation.net.ValidationNetPresenter;
import com.ros.smartrocket.ui.dialog.DefaultInfoDialog;
import com.ros.smartrocket.ui.views.CustomButton;
import com.ros.smartrocket.ui.views.CustomTextView;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.IntentUtils;
import com.ros.smartrocket.utils.LocaleUtils;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.TaskValidationUtils;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.UserActionsLogger;
import com.ros.smartrocket.utils.eventbus.QuitQuestionFlowAction;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class TaskValidationActivity extends BaseActivity implements ValidationLocalMvpView, ValidationNetMvpView, View.OnClickListener {
    @BindView(R.id.closingQuestionText)
    CustomTextView closingQuestionText;
    @BindView(R.id.missionDueTextView)
    CustomTextView missionDueTextView;
    @BindView(R.id.taskDataSizeTextView)
    CustomTextView taskDataSizeTextView;
    @BindView(R.id.dueInTextView)
    CustomTextView dueInTextView;
    @BindView(R.id.taskIdTextView)
    CustomTextView taskIdTextView;
    @BindView(R.id.sendNowButton)
    CustomButton sendNowButton;
    @BindView(R.id.sendLaterButton)
    CustomButton sendLaterButton;
    @BindView(R.id.recheckTaskButton)
    CustomButton recheckTaskButton;

    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private MatrixLocationManager lm = App.getInstance().getLocationManager();
    private Calendar calendar = Calendar.getInstance();


    private int taskId;
    private int missionId;
    private boolean firstlySelection;
    private boolean isRedo;
    private Task task = new Task();
    private float filesSizeB = 0;
    private View actionBarView;

    private ValidationLocalMvpPresenter<ValidationLocalMvpView> localPresenter;
    private ValidationNetMvpPresenter<ValidationNetMvpView> netPresenter;

    public TaskValidationActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_validation);
        ButterKnife.bind(this);
        handleArgs();
        initUI();
        initPresenters();
        localPresenter.getTaskFromDBbyID(taskId, missionId);
    }

    private void initPresenters() {
        localPresenter = new ValidationLocalPresenter<>();
        localPresenter.attachView(this);
        netPresenter = new ValidationNetPresenter<>();
        netPresenter.attachView(this);
    }

    private void initUI() {
        UIUtils.setActivityBackgroundColor(this, getResources().getColor(R.color.white));
        recheckTaskButton.setVisibility(firstlySelection ? View.VISIBLE : View.GONE);
        recheckTaskButton.setOnClickListener(this);
        sendNowButton.setOnClickListener(this);
        sendLaterButton.setOnClickListener(this);
    }

    private void handleArgs() {
        if (getIntent() != null) {
            taskId = getIntent().getIntExtra(Keys.TASK_ID, 0);
            missionId = getIntent().getIntExtra(Keys.MISSION_ID, 0);
            firstlySelection = getIntent().getBooleanExtra(Keys.FIRSTLY_SELECTION, true);
            isRedo = getIntent().getBooleanExtra(Keys.IS_REDO, false);
        }
    }

    @Override
    public void onTaskLoadedFromDb(Task loadedTask) {
        if (loadedTask.getId() != null) {
            task = loadedTask;
            setTaskData();
        } else {
            localPresenter.getTaskFromDBbyID(taskId, missionId);
        }
    }

    @Override
    public void onClosingStatementQuestionLoadedFromDB(List<Question> questions) {
        if (!questions.isEmpty()) {
            Question question = questions.get(0);
            String string = getString(R.string.task_has_not_yet_submitted, "\n" + question.getQuestion());
            closingQuestionText.setMovementMethod(LinkMovementMethod.getInstance());
            closingQuestionText.setText(Html.fromHtml(string));
        } else {
            closingQuestionText.setText(getString(R.string.task_has_not_yet_submitted, ""));
        }
    }

    @Override
    public void setTaskFilesSize(float size) {
        filesSizeB = size;
        taskDataSizeTextView.setText(String.format(Locale.US, "%.1f", size / 1024) + " " + getString(R.string
                .task_data_size_mb));
    }

    public void setTaskData() {
        if (actionBarView != null)
            ((TextView) actionBarView.findViewById(R.id.titleTextView)).setText(task.getName());
        taskIdTextView.setText(String.valueOf(task.getId()));
        long expireTimeInMillisecond = task.getLongExpireDateTime();
        if (expireTimeInMillisecond != 0) {
            long dueInMillisecond = expireTimeInMillisecond - calendar.getTimeInMillis();
            dueInTextView.setText(UIUtils.getTimeInDayHoursMinutes(this, dueInMillisecond));
            missionDueTextView.setText(UIUtils.longToString(expireTimeInMillisecond, 3));
        } else {
            dueInTextView.setVisibility(View.INVISIBLE);
            missionDueTextView.setVisibility(View.INVISIBLE);
        }
        if (firstlySelection) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            localPresenter.getClosingStatementQuestionFromDB(task);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            sendNowButton.setBackgroundResource(R.drawable.button_blue_selector);
            sendLaterButton.setBackgroundResource(R.drawable.button_blue_selector);
            UIUtils.setActionBarBackground(TaskValidationActivity.this, task.getStatusId(), TasksBL.isPreClaimTask(task));
            closingQuestionText.setText(R.string.task_has_not_yet_submitted2);
        }
    }

    public void setFilesToUploadDbAndStartUpload(Boolean use3G) {
        localPresenter.saveFilesToUpload(task, use3G);
        startService(new Intent(TaskValidationActivity.this, UploadFileService.class).setAction(Keys
                .ACTION_CHECK_NOT_UPLOADED_FILES));
    }

    public void sendTextAnswers() {
        if ((UIUtils.is3G(this) && !preferencesManager.getUseOnlyWiFiConnaction()) || UIUtils.isWiFi(this)) {
            netPresenter.sendAnswers(localPresenter.getAnswerListToSend(), missionId);
        } else {
            DialogUtils.showTurnOnWifiDialog(this);
        }
    }

    private void sendAnswerTextsSuccess() {
        if (!Config.USE_BAIDU) {
            AppEventsLogger logger = AppEventsLogger.newLogger(this);
            logger.logEvent(Keys.FB_LOGGING_SUBMITTED);
        }
        localPresenter.updateTaskStatusId(task.getId(), task.getMissionId(), Task.TaskStatusId.COMPLETED.getStatusId());
        if (localPresenter.hasFile()) {
            if (limitExhausted()) {
                showLimitDialog();
            } else {
                setFilesToUploadDbAndStartUpload(true);
                finishActivity();
            }
        } else {
            netPresenter.validateTask(task);
        }
    }

    private void showLimitDialog() {
        DialogUtils.show3GLimitExceededDialog(this, new DefaultInfoDialog.DialogButtonClickListener() {
            @Override
            public void onLeftButtonPressed(Dialog dialog) {
                dialog.dismiss();
                setFilesToUploadDbAndStartUpload(false);
                finishActivity();
            }

            @Override
            public void onRightButtonPressed(Dialog dialog) {
                dialog.dismiss();
                setFilesToUploadDbAndStartUpload(true);
                finishActivity();
            }
        });
    }

    private boolean limitExhausted() {
        return UIUtils.is3G(this)
                && (preferencesManager.get3GUploadTaskLimit() != 0
                && filesSizeB / 1024 > preferencesManager.get3GUploadTaskLimit())
                || (preferencesManager.get3GUploadMonthLimit() != 0
                && preferencesManager.getUsed3GUploadMonthlySize() + filesSizeB / 1024 > preferencesManager.get3GUploadMonthLimit());
    }


    public void finishActivity() {
        if (firstlySelection) {
            PreferencesManager preferencesManager = PreferencesManager.getInstance();
            preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_" + task.getWaveId()
                    + "_" + taskId + "_" + task.getMissionId());
            startActivity(IntentUtils.getMainActivityIntent(this));
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recheckTaskButton:
                localPresenter.updateTaskStatusId(taskId, task.getMissionId(),
                        isRedo ? Task.TaskStatusId.RE_DO_TASK.getStatusId() : Task.TaskStatusId.STARTED.getStatusId());

                Intent intent = isRedo ?
                        IntentUtils.getReCheckReDoQuestionsIntent(this, task.getId(), task.getMissionId())
                        : IntentUtils.getQuestionsIntent(this, task.getId(), task.getMissionId());
                startActivity(intent);
                finish();
                break;
            case R.id.sendNowButton:
                if (isTokenValid())
                    sendNowButtonClick();
                else
                    netPresenter.getNewToken();
                break;
            case R.id.sendLaterButton:
                sendLaterButtonClick();
                break;
            default:
                break;
        }
    }

    private boolean isTokenValid() {
        return !TextUtils.isEmpty(preferencesManager.getTokenForUploadFile()) ||
                !(System.currentTimeMillis() - preferencesManager.getTokenUpdateDate() > DateUtils.HOUR_IN_MILLIS);
    }

    @Override
    public void onTaskLocationSaved(Task savedTask, boolean isSendNow) {
        task = savedTask;
        if (isSendNow)
            sendAnswers();
        else
            finishActivity();
    }

    @Override
    public void onTaskLocationSavedError(Task notSavedTask, String errorText) {
        sendNowButton.setEnabled(true);
        sendLaterButton.setEnabled(true);
        UIUtils.showSimpleToast(TaskValidationActivity.this, errorText);
    }

    public void sendNowButtonClick() {
        if (TaskValidationUtils.isTaskReadyToSend()) {
            sendNowButton.setEnabled(false);
            sendLaterButton.setEnabled(false);
            if (!TaskValidationUtils.isValidationLocationAdded(task)) {
                if (localPresenter.hasFile()) {
                    localPresenter.savePhotoVideoAnswersAverageLocation(task);
                    sendAnswers();
                } else {
                    localPresenter.saveLocationOfTaskToDb(task, true);
                }
            } else {
                sendAnswers();
            }
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        LocaleUtils.setCurrentLanguage();
        if (!UIUtils.isOnline(this)) {
            DialogUtils.showNetworkDialog(this);
        } else if (lm.getLocation() == null
                || !UIUtils.isAllLocationSourceEnabled(this)
                || !UIUtils.isNetworkEnabled(this)
                || !preferencesManager.getUseLocationServices()) {
            DialogUtils.showLocationDialog(this, true);
        } else if (UIUtils.isMockLocationEnabled(this, lm.getLocation())) {
            DialogUtils.showMockLocationDialog(this, true);
        }
    }

    public void sendLaterButtonClick() {
        UserActionsLogger.logTaskSubmitLater(task);
        if (!TaskValidationUtils.isValidationLocationAdded(task) && TaskValidationUtils.isTaskReadyToSend()) {
            if (localPresenter.hasFile()) {
                localPresenter.savePhotoVideoAnswersAverageLocation(task);
                finishActivity();
            } else {
                localPresenter.saveLocationOfTaskToDb(task, false);
            }
        } else {
            finishActivity();
        }
    }

    public void sendAnswers() {
        if (task.getStartedStatusSent())
            sendTextAnswers();
        else
            netPresenter.startTask(task);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!firstlySelection) finish();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!firstlySelection) super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setCustomView(R.layout.actionbar_custom_view_simple_text);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBarView = actionBar.getCustomView();
            if (task != null && !TextUtils.isEmpty(task.getName()))
                ((TextView) actionBarView.findViewById(R.id.titleTextView)).setText(task.getName());
        }
        return true;
    }

    @Override
    public void onNewTokenRetrieved() {
        sendNowButtonClick();
    }

    @Override
    public void onTaskStarted() {
        task.setStartedStatusSent(true);
        localPresenter.updateTaskInDb(task);
        sendTextAnswers();
    }

    @Override
    public void onAnswersSent() {
        sendAnswerTextsSuccess();
    }

    @Override
    public void onAnswersNotSent() {
        sendAnswerTextsSuccess();
    }

    @Override
    public void taskOnValidation() {
        task.setSubmittedAt(UIUtils.longToString(calendar.getTimeInMillis(), 2));
        task.setStatusId(Task.TaskStatusId.VALIDATION.getStatusId());
        localPresenter.updateTaskInDb(task);
        QuestionsBL.removeQuestionsFromDB(task);
        finishActivity();
    }


    @Override
    public void showNetworkError(BaseNetworkError networkError) {
        UIUtils.showSimpleToast(this, networkError.getErrorMessageRes());
        sendNowButton.setEnabled(true);
        sendLaterButton.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        netPresenter.detachView();
        localPresenter.detachView();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(QuitQuestionFlowAction event) {
        if (taskId == event.getTaskId() && missionId == event.getMissionId()) finish();
    }
}
