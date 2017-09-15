package com.ros.smartrocket.flow.question.activity;

import com.ros.smartrocket.App;
import com.ros.smartrocket.bl.QuestionsBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Question;
import com.ros.smartrocket.db.entity.Questions;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.store.QuestionStore;
import com.ros.smartrocket.db.store.ReDoQuestionStore;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

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
                .getReDoQuestions(task.getWaveId(), task.getId(), getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeReDoQuestions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onQuestionsRetrieved, this::showNetworkError));
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
                .subscribe(this::onQuestionsRetrieved, this::showNetworkError));
    }

    @Override
    public void getTaskFromDBbyID(Integer taskId, Integer missionId) {
        showLoading(false);
        addDisposable(TasksBL.getSingleTaskFromDBbyIdObservable(taskId, missionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskLoaded));
    }

    @Override
    public void getQuestionsListFromDB(Task task) {
        addDisposable(QuestionsBL.getQuestionObservable(task, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onQuestionsRetrieved));
    }

    @Override
    public void getWaveFromDB(int waveId) {
        addDisposable(WavesBL.getWaveFromDBbyIdObservable(waveId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWaveLoaded));

    }

    private void onQuestionsRetrieved(List<Question> questions) {
        hideLoading();
        getMvpView().onQuestionsLoadingComplete(questions);
    }

    private void onTaskLoaded(Task task) {
        if (task.getId() != null && task.getId() != 0)
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
