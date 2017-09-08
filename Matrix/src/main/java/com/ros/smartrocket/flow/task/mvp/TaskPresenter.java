package com.ros.smartrocket.flow.task.mvp;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys.MapViewMode;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

import java.util.List;

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
                .subscribe(this::onWavesLoaded, this::showNetworkError));
    }

    @Override
    public void loadTasksFromDb(int itemId, boolean isHidden, MapViewMode mode) {
        switch (mode) {
            case ALL_TASKS:
                getAllNotMyTasks(isHidden);
                break;
            case MY_TASKS:
                getMyTasksForMapFromDB();
                break;
            case WAVE_TASKS:
                getNotMyTasksFromDBbyWaveId(itemId, isHidden);
                break;
            case SINGLE_TASK:
                getTaskFromDBbyID(itemId);
                break;
        }
    }

    private void getAllNotMyTasks(boolean isHidden) {
        addDisposable(TasksBL.getAllNotMyTasksObservable(isHidden)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTasksLoaded));
    }

    private void getMyTasksForMapFromDB() {
        addDisposable(TasksBL.getMyTasksForMapObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTasksLoaded));
    }

    private void getNotMyTasksFromDBbyWaveId(int itemId, boolean isHidden) {
        addDisposable(TasksBL.getNotMyTasksObservable(itemId, isHidden)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTasksLoaded));
    }

    private void getTaskFromDBbyID(int itemId) {
        addDisposable(TasksBL.getTaskFromDBbyIdObservable(itemId, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTasksLoaded));
    }

    private void storeMyWaves(Waves waves) throws Exception {
        wavesStore.storeMyWaves(waves);
    }

    private void onWavesLoaded(Waves waves) {
        getMvpView().onTasksLoaded();
    }

    private void onTasksLoaded(List<Task> tasks) {
        getMvpView().refreshIconState(false);
        getMvpView().onTaskLoadingComplete(tasks);
    }

    @Override
    public void showNetworkError(Throwable t) {
        getMvpView().refreshIconState(false);
        super.showNetworkError(t);
    }
}
