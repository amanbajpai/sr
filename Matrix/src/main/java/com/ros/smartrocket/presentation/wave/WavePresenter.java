package com.ros.smartrocket.presentation.wave;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.task.Waves;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WavePresenter<V extends WaveMvpView> extends BaseNetworkPresenter<V> implements WaveMvpPresenter<V> {
    private WavesStore wavesStore = new WavesStore();

    @Override
    public void getWavesFromServer(double latitude, double longitude, int radius) {
        if (isViewAttached()) {
            getMvpView().refreshIconState(true);
            addDisposable(App.getInstance().getApi()
                    .getWaves(latitude, longitude, radius, getLanguageCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .doOnNext(this::storeWaves)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(this::showNetworkError)
                    .subscribe(w -> onWavesLoaded(), t -> {}));
        }
    }

    private void storeWaves(Waves waves) throws Exception {
        wavesStore.storeWaves(waves);
    }

    private void onWavesLoaded() {
        if (isViewAttached()) {
            getMvpView().refreshIconState(false);
            getMvpView().onWavesLoaded();
        }
    }

    @Override
    public void showNetworkError(Throwable t) {
        super.showNetworkError(t);
        if (isViewAttached()) getMvpView().refreshIconState(false);
    }
}
