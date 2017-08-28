package com.ros.smartrocket.ui.login.location;

import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.ui.base.NetworkMvpView;

public interface CheckLocationMvpView extends NetworkMvpView {

    void onLocationChecked(CheckLocationResponse serverResponse, double latitude, double longitude);

    void onLocationCheckFailed();

    void showLocationCheckDialog();
}
