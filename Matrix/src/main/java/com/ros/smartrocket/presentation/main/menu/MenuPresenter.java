package com.ros.smartrocket.presentation.main.menu;

import android.graphics.Bitmap;

import com.ros.smartrocket.App;
import com.ros.smartrocket.bl.NotificationBL;
import com.ros.smartrocket.bl.TasksBL;
import com.ros.smartrocket.db.entity.UpdateUser;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.BytesBitmap;

import io.reactivex.Observable;
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
    public void updateUserImage(Bitmap avatar) {
        addDisposable(Observable.fromCallable(() -> getUpdateUserImageEntity(avatar))
                .subscribeOn(Schedulers.io())
                .flatMap(us -> App.getInstance().getApi().updateUser(us))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> getMvpView().onUserImageUpdated(), this::onUserUpdateFailed));
    }

    @Override
    public void updateUserName(String name) {
        showLoading(false);
        addDisposable(App.getInstance().getApi().updateUser(getUpdateUserNameEntity(name))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onUserNameUpdated(), this::onUserUpdateFailed));
    }

    @Override
    public void getUnreadNotificationsCount() {
        addDisposable(NotificationBL.unreadNotificationsObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setUnreadNotificationsCount));
    }

    private void setUnreadNotificationsCount(int count) {
        getMvpView().setUnreadNotificationsCount(count);
    }

    private UpdateUser getUpdateUserImageEntity(Bitmap avatar) {
        UpdateUser updateUserEntity = new UpdateUser();
        updateUserEntity.setPhotoBase64(BytesBitmap.getBase64String(avatar));
        return updateUserEntity;
    }

    private UpdateUser getUpdateUserNameEntity(String name) {
        UpdateUser updateUserEntity = new UpdateUser();
        updateUserEntity.setSingleName(name);
        return updateUserEntity;
    }

    private void onUserUpdateFailed(Throwable throwable) {
        showNetworkError(throwable);
        getMvpView().onUserUpdateFailed();
    }

    private void onUserNameUpdated() {
        hideLoading();
        getMvpView().onUserNameUpdated();
    }

}
