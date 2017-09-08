package com.ros.smartrocket.flow.question.activity;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Questions;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.store.QuestionStore;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class QuestionPresenter<V extends QuestionMvpView> extends BaseNetworkPresenter<V> implements QuestionMvpPresenter<V> {
    private QuestionStore questionStore = new QuestionStore();


    @Override
    public void loadTaskFromDBbyID(Integer taskId, Integer missionId) {

    }

    @Override
    public void getReDoQuestions(Task task) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getReDoQuestions(task.getWaveId(), task.getId(), getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeQuestions)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onQuestionsLoaded, this::showNetworkError));
    }

    private void storeQuestions(Questions questions) {

    }

    private void onQuestionsLoaded(Questions questions) {

    }

    @Override
    public void getQuestions(Task task) {

    }

    @Override
    public void getQuestionsListFromDB(Task task) {

    }

    @Override
    public void getWaveFromDB(int waveId) {

    }
}
