package com.ros.smartrocket.presentation.launch;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.Version;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class LaunchPresenter<V extends LaunchMvpView> extends BaseNetworkPresenter<V> implements LaunchMvpPresenter<V> {

    @Override
    public void checkVersion() {
        if (BuildConfig.CHINESE) {
            getAppVersion();
        } else {
            getMvpView().launchApp();
        }
    }

    private void getAppVersion() {
        getMvpView().showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getAppVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleAppVersion, this::showNetworkError));
    }

    private void handleAppVersion(AppVersion appVersion) {
        hideLoading();
        PreferencesManager.getInstance().saveAppVersion(appVersion);
        Version currentVersion = new Version(BuildConfig.VERSION_NAME);
        Version newestVersion = new Version(appVersion.getLatestVersion());
        if (currentVersion.compareTo(newestVersion) < 0) {
            getMvpView().showUpdateAppDialog(currentVersion, newestVersion, appVersion.getLatestVersionLink());
        } else {
            getMvpView().launchApp();
        }
    }
}
