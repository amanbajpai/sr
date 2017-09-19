package com.ros.smartrocket.presentation.validation.local;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.presentation.base.MvpPresenter;

import java.util.List;

public interface ValidationLocalMvpPresenter<V extends ValidationLocalMvpView> extends MvpPresenter<V> {
    void getTaskFromDBbyID(Integer taskId, Integer missionId);

    void getClosingStatementQuestionFromDB(Task task);

    void updateTaskInDb(Task task);

    boolean hasFile();

    void saveFilesToUpload(Task task, boolean use3G);

    List<Answer> getAnswerListToSend();

    void savePhotoVideoAnswersAverageLocation(Task task);

    void updateTaskStatusId(Integer taskId, Integer missionId, Integer statusId);

    void saveLocationOfTaskToDb(Task task, boolean sendNow);
}
