package com.ros.smartrocket.presentation.task;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.task.Waves;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TaskPresenter<V extends TaskMvpView> extends BaseNetworkPresenter<V> implements TaskMvpPresenter<V> {
    private WavesStore wavesStore = new WavesStore();

    @Override
    public void getMyTasksFromServer() {
        getMvpView().refreshIconState(true);
        addDisposable(App.getInstance().getApi()
                .getMyTasks(getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeMyWaves)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::showNetworkError)
                .subscribe(__ -> onTasksLoaded(), t->{}));
    }

    private void storeMyWaves(Waves waves) throws Exception {
        wavesStore.storeMyWaves(waves);
    }

    private void onTasksLoaded() {
        if (isViewAttached()) {
            getMvpView().refreshIconState(false);
            getMvpView().onTasksLoaded();
        }
    }

    @Override
    public void showNetworkError(Throwable t) {
        if (isViewAttached()) {
            hideLoading();
            getMvpView().refreshIconState(false);
            getMvpView().showNetworkError(new NetworkError(t));
        }
    }
}
