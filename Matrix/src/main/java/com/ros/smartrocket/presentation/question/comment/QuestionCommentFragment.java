package com.ros.smartrocket.presentation.question.comment;

import com.ros.smartrocket.R;
import com.ros.smartrocket.presentation.question.base.BaseQuestionFragment;

import butterknife.BindView;

public class QuestionCommentFragment extends BaseQuestionFragment<CommentMvpPresenter<CommentMvpView>, CommentMvpView> {
    @BindView(R.id.commentView)
    CommentView commentView;

    @Override
    public CommentMvpPresenter<CommentMvpView> getPresenter() {
        return new CommentPresenter<>(question);
    }

    @Override
    public CommentMvpView getMvpView() {
        commentView.setPresenter(presenter);
        return commentView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_question_open_comment;
    }

}