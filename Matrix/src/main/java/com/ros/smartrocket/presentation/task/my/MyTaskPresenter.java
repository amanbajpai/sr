package com.ros.smartrocket.presentation.task.my;

import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.presentation.task.TaskMvpView;
import com.ros.smartrocket.presentation.task.TaskPresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class MyTaskPresenter<V extends TaskMvpView> extends TaskPresenter<V> implements MyTaskMvpPresenter<V> {
    @Override
    public void loadMyTasksFromDb() {
        getMvpView().refreshIconState(true);
        addDisposable(TasksBL.getMyTasksObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::OnTasksLoadedFromDb));
    }

    private void OnTasksLoadedFromDb(List<Task> tasks) {
        getMvpView().onTaskLoadingComplete(tasks);
    }
}
