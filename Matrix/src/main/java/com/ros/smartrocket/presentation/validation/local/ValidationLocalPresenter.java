package com.ros.smartrocket.presentation.validation.local;

import android.location.Location;

import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.FilesBL;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.bl.WaitingUploadTaskBL;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.file.NotUploadedFile;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.WaitingUploadTask;
import com.ros.smartrocket.map.CurrentLocatiuonListener;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.TaskValidationUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ValidationLocalPresenter<V extends ValidationLocalMvpView> extends BaseNetworkPresenter<V> implements ValidationLocalMvpPresenter<V> {
    private List<NotUploadedFile> notUploadedFiles = new ArrayList<>();
    private List<Answer> answerListToSend = new ArrayList<>();
    private boolean hasFile = false;
    private float filesSizeB = 0;

    @Override
    public void getTaskFromDBbyID(Integer taskId, Integer missionId) {
        addDisposable(TasksBL.getSingleTaskFromDBbyIdObservable(taskId, missionId)
                .subscribeOn(Schedulers.io())
                .doOnNext(this::handleTask)
                .doOnError(this::showNetworkError)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskLoaded));
    }

    private void handleTask(Task task) {
        answerListToSend = AnswersBL.getAnswersListToSend(task.getId(), task.getMissionId());
        hasFile = AnswersBL.isHasFile(answerListToSend);
        notUploadedFiles = AnswersBL.getTaskFilesListToUpload(task.getId(), task.getMissionId(), task.getName(), task.getLongEndDateTime());
        filesSizeB = AnswersBL.getTaskFilesSizeMb(notUploadedFiles);
        if (!TaskValidationUtils.isValidationLocationAdded(task) && TaskValidationUtils.isTaskReadyToSend())
            AnswersBL.saveValidationLocation(task, answerListToSend, hasFile);
    }

    private void onTaskLoaded(Task task) {
        getMvpView().onTaskLoadedFromDb(task);
        getMvpView().setTaskFilesSize(filesSizeB);
    }

    @Override
    public void getClosingStatementQuestionFromDB(Task task) {
        addDisposable(QuestionsBL.closingStatementQuestionObservable(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onQuestionsRetrieved));
    }

    @Override
    public void updateTaskInDb(Task task) {
        addDisposable(TasksBL.updateTaskObservable(task, task.getMissionId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    public boolean hasFile() {
        return hasFile;
    }

    @Override
    public void saveFilesToUpload(Task task, boolean use3G) {
        WaitingUploadTaskBL.insertWaitingUploadTask(new WaitingUploadTask(task, notUploadedFiles.size()));
        for (NotUploadedFile notUploadedFile : notUploadedFiles) {
            notUploadedFile.setUse3G(use3G);
            notUploadedFile.setWaveId(task.getWaveId());
            notUploadedFile.setLatitudeToValidation(task.getLatitudeToValidation());
            notUploadedFile.setLongitudeToValidation(task.getLongitudeToValidation());
            FilesBL.insertNotUploadedFile(notUploadedFile);
        }
    }

    private void onQuestionsRetrieved(List<Question> questions) {
        getMvpView().onClosingStatementQuestionLoadedFromDB(questions);
    }

    @Override
    public List<Answer> getAnswerListToSend() {
        return answerListToSend;
    }

    @Override
    public void savePhotoVideoAnswersAverageLocation(Task task) {
        AnswersBL.savePhotoVideoAnswersAverageLocation(task, answerListToSend);
    }

    @Override
    public void updateTaskStatusId(Integer taskId, Integer missionId, Integer statusId) {
        TasksBL.updateTaskStatusId(taskId, missionId, statusId);
    }

    @Override
    public void saveLocationOfTaskToDb(Task task, boolean sendNow) {
        showLoading(true);
        MatrixLocationManager.getCurrentLocation(false, new CurrentLocatiuonListener() {
            @Override
            public void getLocationSuccess(Location location) {
                task.setLatitudeToValidation(location.getLatitude());
                task.setLongitudeToValidation(location.getLongitude());
                updateTaskInDb(task);
                if (isViewAttached()) {
                    hideLoading();
                    getMvpView().onTaskLocationSaved(task, sendNow);
                }
            }

            @Override
            public void getLocationFail(String errorText) {
                if (isViewAttached()) {
                    hideLoading();
                    getMvpView().onTaskLocationSavedError(task, errorText);
                }
            }
        });
    }
}
