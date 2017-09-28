package com.ros.smartrocket.presentation.wave.my;

import com.ros.smartrocket.db.bl.WavesBL;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.presentation.wave.WavePresenter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class MyWavePresenter<V extends MyWaveMvpView> extends WavePresenter<V> implements MyWaveMvpPresenter<V> {
    @Override
    public void loadNotMyWavesListFromDB(boolean isHidden) {
        addDisposable(WavesBL.getNotMyWavesListObservableFromDB(isHidden)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWavesLoadedFromDB));
    }

    private void onWavesLoadedFromDB(List<Wave> waves) {
        if (isViewAttached()) {
            getMvpView().refreshIconState(false);
            getMvpView().onWavesLoadingComplete(waves);
        }
    }
}
