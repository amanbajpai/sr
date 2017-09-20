package com.ros.smartrocket.net.retrofit.helper;

import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.RegisterDevice;
import com.ros.smartrocket.utils.PreferencesManager;

import io.reactivex.schedulers.Schedulers;

public class GcmRegistrar {

    public void registerGCMId(String regId, int providerType) {
        RegisterDevice registerDeviceEntity = new RegisterDevice();
        registerDeviceEntity.setDeviceId(PreferencesManager.getInstance().getUUID());
        registerDeviceEntity.setRegistrationId(regId);
        registerDeviceEntity.setProviderType(providerType);
        App.getInstance().getApi().registerGCMId(registerDeviceEntity)
                .subscribeOn(Schedulers.io())
                .subscribe(__ -> done(), this::onError);

    }

    private void done() {
        PreferencesManager.getInstance().setBoolean(Keys.GCM_IS_GCMID_REGISTERED, true);
        Log.e("GCM !!!", "Device registered on server");
    }

    private void onError(Throwable t) {
        Log.e("GcmRegistrar", "Device not registered on server", t);
    }
}
