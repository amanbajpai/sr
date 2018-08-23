package com.ros.smartrocket.net.helper;

import android.util.Log;

import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.db.entity.RegisterDevice;
import com.ros.smartrocket.utils.PreferencesManager;

import io.reactivex.schedulers.Schedulers;

public class FcmRegistrar {

    public void registerFCMId(String regId, int providerType) {
        RegisterDevice registerDeviceEntity = new RegisterDevice();
        registerDeviceEntity.setDeviceId(PreferencesManager.getInstance().getUUID());
        registerDeviceEntity.setRegistrationId(regId);
        registerDeviceEntity.setProviderType(providerType);
        App.getInstance().getApi().registerFCMId(registerDeviceEntity)
                .subscribeOn(Schedulers.io())
                .subscribe(__ -> done(), this::onError);

    }

    private void done() {
        PreferencesManager.getInstance().setBoolean(Keys.FCM_IS_FCM_ID_REGISTERED, true);
        Log.e("FCM !!!", "Device registered on server");
    }

    private void onError(Throwable t) {
        Log.e("FcmRegistrar", "Device not registered on server", t);
    }
}
