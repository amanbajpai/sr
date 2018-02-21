package com.ros.smartrocket.presentation.details.task;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.db.entity.task.Waves;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class TaskDetailPresenter<V extends TaskDetailsMvpView> extends BaseNetworkPresenter<V> implements TaskDetailMvpPresenter<V> {
    private WavesStore wavesStore = new WavesStore();

    @Override
    public void setHideTaskOnMapByID(Integer taskId, Integer missionId, boolean shouldHide) {
        addDisposable(TasksBL.hideTaskOnMapByIdObservable(taskId, missionId, shouldHide)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    @Override
    public void loadTaskFromDBbyID(Integer taskId, Integer missionId) {
        addDisposable(TasksBL.getSingleTaskFromDBbyIdObservable(taskId, missionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskLoaded));
    }

    @Override
    public void loadWaveFromDB(Integer waveId) {
        addDisposable(WavesBL.getWaveFromDBbyIdObservable(waveId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWaveLoaded));
    }

    @Override
    public void getMyTasksFromServer() {
        addDisposable(App.getInstance().getApi()
                .getMyTasks(getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(this::storeMyWaves)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(this::showNetworkError)
                .subscribe(__ -> onTasksLoaded(), t->{}));
    }

    private void onTaskLoaded(Task task) {
        getMvpView().onTaskLoadedFromDb(task);
    }

    private void onWaveLoaded(Wave wave) {
        getMvpView().onWaveLoadedFromDb(wave);
    }

    private void storeMyWaves(Waves waves) throws Exception {
        wavesStore.storeMyWaves(waves);
    }

    private void onTasksLoaded() {
        getMvpView().onTasksLoaded();
    }
}
