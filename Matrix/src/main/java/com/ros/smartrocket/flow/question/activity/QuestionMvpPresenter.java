package com.ros.smartrocket.flow.question.activity;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.MvpPresenter;

interface QuestionMvpPresenter<V extends QuestionMvpView> extends MvpPresenter<V> {

    void loadTaskFromDBbyID(Integer taskId, Integer missionId);

    void getReDoQuestions(Task task);

    void getQuestions(Task task);

    void getQuestionsListFromDB(Task task);

    void getWaveFromDB(int waveId);
}
