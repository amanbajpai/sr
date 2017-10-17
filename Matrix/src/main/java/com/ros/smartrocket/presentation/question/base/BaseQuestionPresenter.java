package com.ros.smartrocket.presentation.question.base;

import android.content.Intent;

import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.presentation.base.BasePresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseQuestionPresenter<V extends BaseQuestionMvpView> extends BasePresenter<V> implements BaseQuestionMvpPresenter<V> {
    protected Question question;
    private Product product;
    private boolean isPreview;
    private boolean isRedo;
    private OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;


    public BaseQuestionPresenter(Question question) {
        this.question = question;
    }

    @Override
    public boolean saveQuestion() {
        if (question != null && question.getAnswers() != null && !question.getAnswers().isEmpty()) {
            addDisposable(AnswersBL.getUpdateAnswersInDBObservable(question.getAnswers())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onAnswersUpdated));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public void loadAnswers() {
        addDisposable(AnswersBL.getAnswersListFromDBObservable(question, product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAnswersLoadedFromDb, t -> {})
        );
    }

    @Override
    public void addEmptyAnswer() {
        Answer answer = new Answer();
        answer.setRandomId();
        answer.setQuestionId(question.getId());
        answer.setTaskId(question.getTaskId());
        answer.setMissionId(question.getMissionId());
        answer.setProductId(product != null ? product.getId() : 0);
        if (!isPreview) answer.set_id(AnswersBL.insert(answer));
        question.getAnswers().add(answer);
    }

    @Override
    public void onAnswersLoadedFromDb(List<Answer> answers) {
        question.setAnswers(answers);
        getMvpView().fillViewWithAnswers(answers);
    }

    @Override
    public void deleteAnswer(Answer answer) {
        addDisposable(AnswersBL.getDeleteAnswerFromDBObservable(answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAnswersDeleted));
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        return false;
    }

    @Override
    public boolean isRedo() {
        return isRedo;
    }

    @Override
    public void setRedo(boolean redo) {
        isRedo = redo;
    }

    @Override
    public boolean isPreview() {
        return isPreview;
    }

    @Override
    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    @Override
    public void refreshNextButton(boolean isSelected) {
        if (answerSelectedListener != null)
            answerSelectedListener.onAnswerSelected(isSelected, question.getId());

        if (answerPageLoadingFinishedListener != null)
            answerPageLoadingFinishedListener.onAnswerPageLoadingFinished();
    }

    @Override
    public void setAnswerSelectedListener(OnAnswerSelectedListener answerSelectedListener) {
        this.answerSelectedListener = answerSelectedListener;
    }

    @Override
    public void setAnswerPageLoadingFinishedListener(OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener) {
        this.answerPageLoadingFinishedListener = answerPageLoadingFinishedListener;
    }

    @Override
    public void onAnswersDeleted() {

    }

    @Override
    public void onAnswersUpdated() {

    }

    @Override
    public void attachView(V mvpView) {
        super.attachView(mvpView);
        getMvpView().configureView(question);
        getMvpView().validateView(question);
    }
}
