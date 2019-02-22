package com.ros.smartrocket.presentation.question.base;

import android.content.Intent;
import android.util.Log;

import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.bl.CustomFieldImageUrlBL;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.question.CustomFieldImageUrls;
import com.ros.smartrocket.db.entity.question.Product;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.interfaces.OnAnswerPageLoadingFinishedListener;
import com.ros.smartrocket.interfaces.OnAnswerSelectedListener;
import com.ros.smartrocket.presentation.base.BasePresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseQuestionPresenter<V extends BaseQuestionMvpView> extends BasePresenter<V> implements BaseQuestionMvpPresenter<V> {
    protected Question question;
    protected Product product;
    private boolean isPreview;
    private boolean isRedo;
    protected OnAnswerSelectedListener answerSelectedListener;
    private OnAnswerPageLoadingFinishedListener answerPageLoadingFinishedListener;


    public BaseQuestionPresenter(Question question) {
        this.question = question;
    }

    @Override
    public boolean saveQuestion() {
        if (question != null && question.getAnswers() != null && !question.getAnswers().isEmpty()) {
            AnswersBL.updateAnswersInDB(question.getAnswers());
            CustomFieldImageUrlBL.updateCustomFieldImageUrlInDB(question.getCustomFieldImages());
            onAnswersUpdated();
            return true;
        } else {
            return false;
        }


//        if (question != null && question.getCustomFieldImages() != null && !question.getCustomFieldImages().isEmpty()) {
//            CustomFieldImageUrlBL.updateCustomFieldImageUrlInDB(question.getCustomFieldImages());
//            onAnswersUpdated();
//            return true;
//        } else {
//            return false;
//        }

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
                .subscribe(this::onAnswersLoadedFromDb, this::logAnswerLoadingFail)
        );
    }

    @Override
    public void loadCustomFieldImageUrlsList() {
        addDisposable(CustomFieldImageUrlBL.getCustomFieldImageUrlFromDBObservable(question, product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCustomFieldImageURlLoadedFromDb, this::logAnswerLoadingFail));
    }

    private int logAnswerLoadingFail(Throwable t) {
        return Log.e("BaseQuestion", "Loading of Answers from DB is FAILED", t);
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
    public void onCustomFieldImageURlLoadedFromDb(List<CustomFieldImageUrls> customFieldImageUrls) {
        question.setCustomFieldImages(customFieldImageUrls);
        getMvpView().fillViewWithCustomFieldImageUrls(customFieldImageUrls);
    }


    @Override
    public void deleteAnswer(Answer answer) {
        addDisposable(AnswersBL.deleteAnswerFromDBObservable(answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAnswersDeleted));
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
        return false;
    }

    @Override
    public void setProduct(Product product) {
        this.product = product;
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
