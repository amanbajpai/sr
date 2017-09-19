package com.ros.smartrocket.presentation.task.map;

import com.ros.smartrocket.Keys.MapViewMode;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.presentation.task.TaskMvpView;
import com.ros.smartrocket.presentation.task.TaskPresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class MapTaskPresenter<V extends TaskMvpView> extends TaskPresenter<V> implements MapTaskMvpPresenter<V> {

    @Override
    public void loadTasksFromDb(int itemId, boolean isHidden, MapViewMode mode) {
        if (isViewAttached()) {
            getMvpView().refreshIconState(true);
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

    private void onTasksLoaded(List<Task> tasks) {
        getMvpView().onTaskLoadingComplete(tasks);
    }

    @Override
    public void showNetworkError(Throwable t) {
        getMvpView().refreshIconState(false);
        super.showNetworkError(t);
    }
}
