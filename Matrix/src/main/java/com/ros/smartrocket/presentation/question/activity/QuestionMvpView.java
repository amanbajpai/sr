package com.ros.smartrocket.presentation.question.activity;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

import java.util.List;

interface QuestionMvpView extends NetworkMvpView {

    void onTaskLoadedFromDb(Task task);

    void onWaveLoadingComplete(Wave wave);

    void onQuestionsLoadingComplete(List<Question> questions);

    void onQuestionsLoaded();

}
