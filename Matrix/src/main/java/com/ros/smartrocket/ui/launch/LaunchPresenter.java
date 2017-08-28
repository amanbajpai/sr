package com.ros.smartrocket.ui.launch;

import com.ros.smartrocket.App;
import com.ros.smartrocket.BuildConfig;
import com.ros.smartrocket.db.entity.AppVersion;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.base.BasePresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.Version;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class LaunchPresenter<V extends LaunchMvpView> extends BasePresenter<V> implements LaunchMvpPresenter<V> {

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
        Call<AppVersion> call = App.getInstance().getApi().getAppVersion();
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                getMvpView().hideLoading();
                if (response.isSuccessful()) {
                    handleAppVersion(response.body());
                } else {
                    getMvpView().showNetworkError(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                getMvpView().hideLoading();
                getMvpView().showNetworkError(new NetworkError(t));
            }
        });
    }

    private void handleAppVersion(AppVersion appVersion) {
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
