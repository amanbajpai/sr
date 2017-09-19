package com.ros.smartrocket.presentation.question.main;

import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.presentation.base.MvpPresenter;

interface QuestionMvpPresenter<V extends QuestionMvpView> extends MvpPresenter<V> {

    void loadReDoQuestions(Task task);

    void loadQuestions(Task task);

    void getQuestionsListFromDB(Task task);

    void getWaveFromDB(int waveId);

    void getTaskFromDBbyID(Integer taskId, Integer missionId);
}
