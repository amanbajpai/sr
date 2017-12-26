package com.ros.smartrocket.presentation.question.main;

import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

import java.util.List;

interface QuestionMvpView extends NetworkMvpView {

    void onTaskLoadedFromDb(Task task);

    void onWaveLoadingComplete(Wave wave);

    void onQuestionsLoadingComplete(List<Question> questions);

    void onQuestionsLoaded();

}
