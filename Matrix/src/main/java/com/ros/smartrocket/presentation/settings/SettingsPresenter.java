package com.ros.smartrocket.presentation.settings;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.AllowPushNotification;
import com.ros.smartrocket.db.entity.account.MyAccount;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class SettingsPresenter<V extends SettingsMvpView> extends BaseNetworkPresenter<V> implements SettingsMvpPresenter<V> {
    private boolean isAllowed;

    @Override
    public void allowPushNotifications(boolean isAllowed) {
        showLoading(false);
        this.isAllowed = isAllowed;
        addDisposable(App.getInstance().getApi()
                .allowPushNotification(new AllowPushNotification(isAllowed))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onPushStatusChanges(), this::showNetworkError));
    }

    private void onPushStatusChanges() {
        MyAccount myAccount = App.getInstance().getMyAccount();
        myAccount.setAllowPushNotification(isAllowed);
        PreferencesManager.getInstance().setUsePushMessages(isAllowed);
        App.getInstance().setMyAccount(myAccount);
        hideLoading();
        getMvpView().onPushStatusChanged();
    }

    @Override
    public void closeAccount() {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .closeAccount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onAccountClosed(), this::showNetworkError));
    }

    private void onAccountClosed() {
        hideLoading();
        getMvpView().onAccountClosed();
    }
}
