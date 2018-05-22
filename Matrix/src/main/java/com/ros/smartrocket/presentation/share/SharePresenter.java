package com.ros.smartrocket.presentation.share;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SharePresenter<V extends ShareMvpView> extends BaseNetworkPresenter<V> implements ShareMvpPresenter<V> {
    @Override
    public void getSharingData() {
        int appType = 1;
        if (BuildConfig.CHINESE) {
            appType = 2;
        }
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getSharingData(getLanguageCode(), appType) //App type 1 for Global, 2 for China
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSharingLoaded, this::showNetworkError));
    }

    private void onSharingLoaded(Sharing sharing) {
        hideLoading();
        getMvpView().onSharingLoaded(sharing);
    }
}
