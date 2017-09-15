package com.ros.smartrocket.flow.question.activity;

import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.flow.base.NetworkMvpView;

import java.util.List;

interface QuestionMvpView extends NetworkMvpView {

    void onTaskLoadedFromDb(Task task);

    void onWaveLoadingComplete(Wave wave);

    void onQuestionsLoadingComplete(List<Question> questions);

    void onQuestionsLoaded();

}
