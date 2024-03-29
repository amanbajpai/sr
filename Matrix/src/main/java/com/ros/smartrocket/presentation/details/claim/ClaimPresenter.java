package com.ros.smartrocket.presentation.details.claim;

import android.content.Context;
import android.location.Location;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.question.Questions;
import com.ros.smartrocket.db.entity.task.ClaimTaskResponse;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.db.store.QuestionStore;
import com.ros.smartrocket.map.CurrentLocationListener;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.SendTaskIdMapper;
import com.ros.smartrocket.utils.UIUtils;
import com.ros.smartrocket.utils.UserActionsLogger;
import com.ros.smartrocket.utils.helpers.ClaimWarningParser;

import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ClaimPresenter<V extends ClaimMvpView> extends BaseNetworkPresenter<V> implements ClaimMvpPresenter<V> {
    private Task task;
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();
    private boolean instructionMediaLoaded;
    private boolean massAuditMediaLoaded;
    private Location location;

    public Context context;

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public void claimTask() {
        showLoading(false);
        getQuestions();
    }

    private void getQuestions() {
        addDisposable(App.getInstance().getApi()
                .getQuestions(task.getWaveId(), task.getId(), getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeQuestions)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::showNetworkError)
                .subscribe(__ -> findLocation(), t -> {
                }));
    }

    private void findLocation() {
        MatrixLocationManager.getCurrentLocation(false, new CurrentLocationListener() {
            @Override
            public void getLocationSuccess(Location location) {
                if (!isViewAttached()) return;
                ClaimPresenter.this.location = location;
                loadWaveFromDB(task.getWaveId());
            }

            @Override
            public void getLocationFail(String errorText) {
                if (!isViewAttached()) return;
                hideLoading();
                UIUtils.showSimpleToast(App.getInstance(), errorText);
            }
        });
    }

    private void loadWaveFromDB(Integer waveId) {
        addDisposable(WavesBL.getWaveFromDBbyIdObservable(waveId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWaveLoaded, this::onError));
    }

    private void onWaveLoaded(Wave wave) {
        if (wave.getDownloadMediaWhenClaimingTask()
                && wave.getMissionSize() != null
                && wave.getMissionSize() != 0) {
            getMvpView().showDownloadMediaDialog(wave);
        } else {
            claimTaskRequest();
        }
    }

    private void claimTaskRequest() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .claimTask(SendTaskIdMapper.getSendTaskIdForClaim(task, location))
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .doOnNext(r -> storeQuestions(r.getQuestions()))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::showNetworkError)
                .subscribe(this::handleClaimResponse, t -> {
                }));
    }

    @Override
    public void showNetworkError(Throwable t) {
        NetworkError networkError = new NetworkError(t);
        if (networkError.getErrorCode() == NetworkError.TASK_IS_CLAIMED_ERROR) {
            hideLoading();
            getMvpView().showTaskAlreadyClaimedDialog();
        } else {
            super.showNetworkError(t);
        }
    }

    private void handleClaimResponse(ClaimTaskResponse response) {
        new ClaimWarningParser().parseWarnings(response.getWarnings());
        long startTimeInMillisecond = task.getLongStartDateTime();
        long preClaimedExpireInMillisecond = task.getLongPreClaimedTaskExpireAfterStart();
        long claimTimeInMillisecond = Calendar.getInstance().getTimeInMillis();
        long timeoutInMillisecond = task.getLongExpireTimeoutForClaimedTask();
        long missionDueMillisecond;

        if (TasksBL.isPreClaimTask(task))
            missionDueMillisecond = startTimeInMillisecond + preClaimedExpireInMillisecond;
        else
            missionDueMillisecond = claimTimeInMillisecond + timeoutInMillisecond;
        task.setMissionId(response.getMissionId());
        task.setStatusId(Task.TaskStatusId.CLAIMED.getStatusId());
        task.setIsMy(true);
        task.setClaimed(UIUtils.longToString(claimTimeInMillisecond, 2));
        task.setLongClaimDateTime(claimTimeInMillisecond);
        task.setExpireDateTime(UIUtils.longToString(missionDueMillisecond, 2));
        task.setLongExpireDateTime(missionDueMillisecond);
        updateTask(task, null);

        QuestionsBL.setMissionId(task.getWaveId(), task.getId(), task.getMissionId());
        AnswersBL.setMissionId(task.getId(), task.getMissionId());

        getMvpView().showClaimDialog(UIUtils.longToString(missionDueMillisecond, 3));
        hideLoading();
    }

    @Override
    public void downloadMedia() {
        addDisposable(QuestionsBL.getQuestionObservable(task, false)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onQuestionsRetrieved, this::onError));
    }

    private void onQuestionsRetrieved(List<Question> questions) {
        List<Question> instructionQuestions = QuestionsBL.getInstructionQuestionList(questions);
        List<Question> massAuditQuestions = QuestionsBL.getMassAuditQuestionList(questions);
        if (!instructionQuestions.isEmpty())
            downloadInstructionQuestionFile(instructionQuestions);
        else
            instructionMediaLoaded = true;

        if (!massAuditQuestions.isEmpty()) {
            downloadMassAuditProductFile(massAuditQuestions);
        } else {
            massAuditMediaLoaded = true;
            tryToClaim();
        }
    }

    private void downloadMassAuditProductFile(List<Question> massAuditQuestions) {
        addDisposable(MediaDownloader.getDownloadMassAuditProductFileObservable(massAuditQuestions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ ->
                {
                    massAuditMediaLoaded = true;
                    tryToClaim();
                }, this::onError));
    }

    private void downloadInstructionQuestionFile(List<Question> instructionQuestions) {
        addDisposable(MediaDownloader.getDownloadInstructionQuestionsObservable(instructionQuestions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ ->
                {
                    instructionMediaLoaded = true;
                    tryToClaim();
                }, this::onError));
    }

    private void tryToClaim() {
        if (instructionMediaLoaded && massAuditMediaLoaded) claimTaskRequest();
    }

    private void updateTask(Task task, Integer missionId) {
        addDisposable(TasksBL.updateTaskObservable(task, missionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    private void removeAnswersByTaskId() {
        AnswersBL.getRemoveAnswersByTaskIdObservable(task.getId())
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void removeQuestionsByTaskFromDB() {
        QuestionsBL.getRemoveQuestionsObservable(task)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void storeQuestions(Questions questions) {
        QuestionStore store = new QuestionStore(task);
        store.storeQuestions(questions);
    }

    // --------------//

    @Override
    public void unClaimTask() {
        getMvpView().showUnClaimDialog();
    }

    @Override
    public void unClaimTaskRequest() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .unClaimTask(SendTaskIdMapper.getSendTaskIdForUnClaim(task))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onTaskUnclaimed(), this::showNetworkError));
    }

    private void onTaskUnclaimed() {
        hideLoading();
        if (task != null) UserActionsLogger.logTaskWithdraw(task);
        changeStatusToUnClaimed();
    }

    @Override
    public void startTask() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .startTask(SendTaskIdMapper.getSendTaskIdForStart(task))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onTaskStarted(), this::showNetworkError));
    }

    private void onTaskStarted() {
        hideLoading();
        changeStatusToStarted(true);
    }

    private void changeStatusToStarted(boolean startedStatusSent) {
        task.setStatusId(Task.TaskStatusId.STARTED.getStatusId());
        task.setStarted(UIUtils.longToString(Calendar.getInstance().getTimeInMillis(), 2));
        task.setStartedStatusSent(startedStatusSent);
        updateTask(task, task.getMissionId());
        getMvpView().onTaskStarted(task);
    }

    private void changeStatusToUnClaimed() {
        preferencesManager.remove(Keys.LAST_NOT_ANSWERED_QUESTION_ORDER_ID + "_"
                + task.getWaveId() + "_"
                + task.getId() + "_"
                + task.getMissionId());

        Integer missionId = task.getMissionId();
        task.setStatusId(Task.TaskStatusId.NONE.getStatusId());
        task.setStarted("");
        task.setIsMy(false);
        task.setMissionId(null);
        updateTask(task, missionId);
        removeQuestionsByTaskFromDB();
        removeAnswersByTaskId();
        getMvpView().onTaskUnclaimed();
    }
}
