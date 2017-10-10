package com.ros.smartrocket.presentation.question.base;

import android.content.ContentUris;
import android.net.Uri;

import com.ros.smartrocket.db.AnswerDbSchema;
import com.ros.smartrocket.db.bl.AnswersBL;
import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Product;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.presentation.base.BasePresenter;

import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseQuestionPresenter<V extends BaseQuestionMvpView> extends BasePresenter<V> implements BaseQuestionMvpPresenter<V> {
    protected Question question;
    private Product product;
    private boolean isPreview;


    public BaseQuestionPresenter(Question question, boolean isPreview) {
        this.isPreview = isPreview;
        this.question = question;
    }

    @Override
    public boolean saveQuestion() {
        return false;
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
                .subscribe(this::onAnswersLoadedFromDb, t -> {
                })
        );
    }

    @Override
    public List<Answer> addEmptyAnswer(List<Answer> currentAnswerArray) {
        Answer answer = new Answer();
        answer.setRandomId();
        answer.setQuestionId(question.getId());
        answer.setTaskId(question.getTaskId());
        answer.setMissionId(question.getMissionId());
        answer.setProductId(product != null ? product.getId() : 0);

        if (!isPreview) {
            Uri uri = getActivity().getContentResolver().insert(AnswerDbSchema.CONTENT_URI, answer.toContentValues());
            long id = ContentUris.parseId(uri);
            answer.set_id(id);
        }

        Answer[] resultAnswerArray = Arrays.copyOf(currentAnswerArray, currentAnswerArray.length + 1);
        resultAnswerArray[currentAnswerArray.length] = answer;
        return resultAnswerArray;
    }

    private void onAnswersLoadedFromDb(List<Answer> answers) {
        getMvpView().fillViewWithAnswers(answers);
    }
}
