package com.ros.smartrocket.presentation.validation.net;

import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.presentation.base.MvpPresenter;

import java.util.List;

public interface ValidationNetMvpPresenter<V extends ValidationNetMvpView> extends MvpPresenter<V> {
    void validateTask(Task task);

    void getNewToken();

    void startTask(Task task);

    void sendAnswers(List<Answer> answers, Integer missionId);
}
