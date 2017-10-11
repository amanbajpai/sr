package com.ros.smartrocket.presentation.question.comment;

import com.ros.smartrocket.presentation.question.base.BaseQuestionMvpPresenter;

public interface CommentMvpPresenter<V extends CommentMvpView> extends BaseQuestionMvpPresenter<V> {
    void onCommentEntered(String s);
}
