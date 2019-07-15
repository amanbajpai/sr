package com.ros.smartrocket.presentation.question.base;

import android.content.Intent;

import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Product;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.presentation.base.MvpPresenter;

import java.util.List;

public interface BaseQuestionMvpPresenter<V extends BaseQuestionMvpView> extends MvpPresenter<V> {
    boolean saveQuestion();

    Question getQuestion();

    void loadAnswers();

    void loadCustomFieldImageUrlsList();

    void addEmptyAnswer();

    boolean isRedo();

    void setRedo(boolean redo);

    boolean isPreview();

    void setPreview(boolean preview);

    void refreshNextButton(boolean isSelected);

    void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener);

    void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener listener);

    void onAnswersDeleted();

    void onAnswersUpdated();

    void onAnswersLoadedFromDb(List<Answer> answers);

    void onCustomFieldImageURlLoadedFromDb(List<CustomFieldImageUrls> customFieldImageUrls);

    void deleteAnswer(Answer answer);

    boolean onActivityResult(int requestCode, int resultCode, Intent intent);

    void setProduct(Product product);
}
