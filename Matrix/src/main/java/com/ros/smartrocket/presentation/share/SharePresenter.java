package com.ros.smartrocket.presentation.share;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Sharing;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SharePresenter<V extends ShareMvpView> extends BaseNetworkPresenter<V> implements ShareMvpPresenter<V> {
    @Override
    public void getSharingData() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getSharingData(getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSharingLoaded, this::showNetworkError));
    }

    private void onSharingLoaded(Sharing sharing) {
        hideLoading();
        getMvpView().onSharingLoaded(sharing);
    }
}
