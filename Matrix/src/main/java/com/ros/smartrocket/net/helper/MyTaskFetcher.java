package com.ros.smartrocket.net.helper;

import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.store.WavesStore;
import com.ros.smartrocket.utils.PreferencesManager;

import io.reactivex.schedulers.Schedulers;

public class MyTaskFetcher {
    public void getMyTasksFromServer() {
        App.getInstance().getApi()
                .getMyTasks(PreferencesManager.getInstance().getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(w -> new WavesStore().storeMyWaves(w))
                .subscribe(__ -> {
                }, this::onError);
    }

    private void onError(Throwable t) {
        Log.e("MyTaskFetcher", "Device not registered on server", t);
    }
}
