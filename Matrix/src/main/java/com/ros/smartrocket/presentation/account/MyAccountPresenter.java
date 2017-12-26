package com.ros.smartrocket.presentation.account;

import android.graphics.Bitmap;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.account.UpdateUser;
import com.ros.smartrocket.presentation.account.base.AccountPresenter;
import com.ros.smartrocket.utils.BytesBitmap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MyAccountPresenter<V extends MyAccountMvpView> extends AccountPresenter<V> implements MyAccountMvpPresenter<V> {

    public MyAccountPresenter(boolean showProgress) {
        super(showProgress);
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
