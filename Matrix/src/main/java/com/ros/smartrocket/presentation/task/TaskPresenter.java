package com.ros.smartrocket.presentation.task;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TaskPresenter<V extends TaskMvpView> extends BaseNetworkPresenter<V> implements TaskMvpPresenter<V> {
    private WavesStore wavesStore = new WavesStore();

    @Override
    public void getMyTasksFromServer() {
        addDisposable(App.getInstance().getApi()
                .getMyTasks(getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeMyWaves)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onTasksLoaded(), this::showNetworkError));
    }

    private void storeMyWaves(Waves waves) throws Exception {
        wavesStore.storeMyWaves(waves);
    }

    private void onTasksLoaded() {
        getMvpView().onTasksLoaded();
    }
}
