package com.ros.smartrocket.flow.details.task;

import com.ros.smartrocket.App;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;

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
                .subscribe(__ -> onTasksLoaded(), this::showNetworkError));
    }

    private void onTaskLoaded(Task task) {
        if (task.getId() != null && task.getId() != 0)
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
