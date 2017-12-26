package com.ros.smartrocket.presentation.question.comment;

import android.text.TextUtils;

import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionPresenter;

public class CommentPresenter<V extends CommentMvpView> extends BaseQuestionPresenter<V> implements CommentMvpPresenter<V> {

    public CommentPresenter(Question question) {
        super(question);
    }

    @Override
    public boolean saveQuestion() {
        if (question != null) question.setFirstAnswer(getMvpView().getAnswerValue());
        return super.saveQuestion();
    }

    @Override
    public void onCommentEntered(String s) {
        refreshNextButton(!TextUtils.isEmpty(s.trim()));
    }
}
