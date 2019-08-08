package com.ros.smartrocket.presentation.question.base;

import android.os.Bundle;

import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.base.MvpView;

import java.util.List;

public interface BaseQuestionMvpView extends MvpView {
    void validateView(Question question);

    void configureView(Question question);

    void fillViewWithAnswers(List<Answer> answers);

    void fillViewWithCustomFieldImageUrls(List<CustomFieldImageUrls> customFieldImageUrlsList);

    void onDestroy();

    String getAnswerValue();

    void onPause();

    void onStart();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void setInstanceState(Bundle outState);
}
