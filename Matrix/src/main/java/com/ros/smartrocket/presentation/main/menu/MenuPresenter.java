package com.ros.smartrocket.presentation.main.menu;

import com.ros.smartrocket.db.bl.NotificationBL;
import com.ros.smartrocket.db.bl.TasksBL;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class MenuPresenter<V extends MenuMvpView> extends BaseNetworkPresenter<V> implements MenuMvpPresenter<V> {

    @Override
    public void getMyTasksCount() {
        addDisposable(TasksBL.myTaskCountObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setMyTaskCount));
    }

    private void setMyTaskCount(int count) {
        getMvpView().setMyTasksCount(count);
    }

    @Override
    public void getUnreadNotificationsCount() {
        addDisposable(NotificationBL.unreadNotificationsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setUnreadNotificationsCount));
    }

    private void setUnreadNotificationsCount(int count) {
        if (isViewAttached())
            getMvpView().setUnreadNotificationsCount(count);
    }
}
