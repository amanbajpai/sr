package com.ros.smartrocket.presentation.question.base;

import android.os.Bundle;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.base.MvpView;

import java.util.List;

public interface BaseQuestionMvpView extends MvpView {
    void validateView(Question question);

    void configureView(Question question);

    void fillViewWithAnswers(List<Answer> answers);

    void onDestroy();

    String getAnswerValue();

    void onPause();

    void onStart();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void setInstanceState(Bundle outState);
}
