package com.ros.smartrocket.flow.validation.net;

import android.app.Activity;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.MvpPresenter;

import java.util.List;

public interface ValidationNetMvpPresenter<V extends ValidationNetMvpView> extends MvpPresenter<V> {
    void validateTask(Task task);

    void getNewToken();

    void startTask(Task task);

    void sendAnswers(List<Answer> answers, Integer missionId);
}
