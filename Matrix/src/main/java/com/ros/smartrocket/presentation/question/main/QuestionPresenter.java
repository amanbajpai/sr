package com.ros.smartrocket.presentation.question.main;

import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.bl.QuestionsBL;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.question.Question;
import com.ros.smartrocket.db.entity.question.Questions;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.db.store.QuestionStore;
import com.ros.smartrocket.db.store.ReDoQuestionStore;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class QuestionPresenter<V extends QuestionMvpView> extends BaseNetworkPresenter<V> implements QuestionMvpPresenter<V> {
    private Task task;

    @Override
    public void loadReDoQuestions(Task task) {
        showLoading(false);
        this.task = task;
        addDisposable(App.getInstance().getApi()
                .getReDoQuestions(task.getMissionId(), task.getId(), getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeReDoQuestions)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::showNetworkError)
                .subscribe(this::onQuestionsRetrieved, t->{}));
    }

    @Override
    public void loadQuestions(Task task) {
        showLoading(false);
        this.task = task;
        addDisposable(App.getInstance().getApi()
                .getQuestions(task.getWaveId(), task.getId(), getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeQuestions)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::showNetworkError)
                .subscribe(this::onQuestionsRetrieved, t->{}));
    }

    @Override
    public void getTaskFromDBbyID(Integer taskId, Integer missionId) {
        showLoading(false);
        addDisposable(TasksBL.getSingleTaskFromDBbyIdObservable(taskId, missionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskLoaded, this::onError));
    }

    @Override
    public void getQuestionsListFromDB(Task task) {
        addDisposable(QuestionsBL.getQuestionObservable(task, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onQuestionsRetrieved, this::onError));
    }

    @Override
    public void getWaveFromDB(int waveId) {
        addDisposable(WavesBL.getWaveFromDBbyIdObservable(waveId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWaveLoaded, this::onError));

    }

    private void onQuestionsRetrieved(List<Question> questions) {
        hideLoading();
        getMvpView().onQuestionsLoadingComplete(questions);
    }

    private void onTaskLoaded(Task task) {
        getMvpView().onTaskLoadedFromDb(task);
    }

    private void onWaveLoaded(Wave wave) {
        getMvpView().onWaveLoadingComplete(wave);
    }

    private void storeReDoQuestions(Questions questions) {
        ReDoQuestionStore store = new ReDoQuestionStore(task);
        store.storeQuestions(questions);
    }

    private void storeQuestions(Questions questions) {
        QuestionStore store = new QuestionStore(task);
        store.storeQuestions(questions);
    }

    private void onQuestionsRetrieved(Questions questions) {
        getMvpView().onQuestionsLoaded();
    }

}
