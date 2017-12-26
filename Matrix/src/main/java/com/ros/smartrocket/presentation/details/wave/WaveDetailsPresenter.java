package com.ros.smartrocket.presentation.details.wave;

import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.db.entity.task.Wave;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class WaveDetailsPresenter<V extends WaveDetailsMvpView> extends BaseNetworkPresenter<V> implements WaveDetailsMvpPresenter<V> {

    @Override
    public void loadWaveWithNearTaskFromDB(Integer waveId) {
        showLoading(false);
        addDisposable(WavesBL.getWaveWithNearTaskFromDbObservable(waveId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWaveLoaded));
    }

    private void onWaveLoaded(Wave wave) {
        hideLoading();
        getMvpView().onWaveLoadedFromDb(wave);
    }

    @Override
    public void setHideAllProjectTasksOnMapByID(Integer waveId, boolean shouldHide) {
        addDisposable(TasksBL.hideAllProjectTasksOnMapByIdObservable(waveId, shouldHide)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onTaskVisibilityChanged(shouldHide)));
    }

    private void onTaskVisibilityChanged(boolean isHided) {
        if (isHided)
            getMvpView().onTasksHided();
        else
            getMvpView().onTasksUnHided();
    }

    @Override
    public void loadTaskFromDBbyID(Integer taskId, Integer missionId) {
        addDisposable(TasksBL.getSingleTaskFromDBbyIdObservable(taskId, missionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onTaskLoaded));
    }

    private void onTaskLoaded(Task task) {
        if (task.getId() != null && task.getId() != 0)
            getMvpView().onNearTaskLoadedFromDb(task);
    }
}
