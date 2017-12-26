package com.ros.smartrocket.presentation.question.comment;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.AttributeSet;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.ros.smartrocket.R;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.presentation.question.base.BaseQuestionView;
import com.ros.smartrocket.ui.views.CustomEditTextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CommentView extends BaseQuestionView<CommentMvpPresenter<CommentMvpView>> implements CommentMvpView {
    public static final int TIMEOUT = 100;
    @BindView(R.id.answerEditText)
    CustomEditTextView answerEditText;
    private Disposable commentDisposable;

    public CommentView(Context context) {
        super(context);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.view_question_comment;
    }

    @Override
    public void configureView(Question question) {
        setEditTextWatcher();
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(question.getMaximumCharacters());
        answerEditText.setFilters(filterArray);
        presenter.loadAnswers();
    }

    @Override
    public void fillViewWithAnswers(List<Answer> answers) {
        if (answers.get(0).getChecked()) answerEditText.setText(answers.get(0).getValue());
        presenter.onCommentEntered(answerEditText.getText().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (commentDisposable != null && !commentDisposable.isDisposed())
            commentDisposable.dispose();
    }

    @Override
    public String getAnswerValue() {
        return answerEditText.getText().toString();
    }

    private void setEditTextWatcher() {
        commentDisposable = RxTextView.textChanges(answerEditText)
                .debounce(TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> presenter.onCommentEntered(s.toString()));
    }
}
