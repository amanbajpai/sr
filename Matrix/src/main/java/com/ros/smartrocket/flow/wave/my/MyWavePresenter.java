package com.ros.smartrocket.flow.wave.my;

import com.ros.smartrocket.bl.WavesBL;
import com.ros.smartrocket.db.entity.Wave;
import com.ros.smartrocket.db.entity.Waves;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.flow.wave.WavePresenter;

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
        getMvpView().refreshIconState(false);
        getMvpView().onWavesLoadingComplete(waves);
    }
}
