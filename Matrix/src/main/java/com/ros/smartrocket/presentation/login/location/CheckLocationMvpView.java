package com.ros.smartrocket.presentation.login.location;

import com.ros.smartrocket.db.entity.location.CheckLocationResponse;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

public interface CheckLocationMvpView extends NetworkMvpView {

    void onLocationChecked(CheckLocationResponse serverResponse, double latitude, double longitude);

    void onLocationCheckFailed();

    void showLocationCheckDialog();
}
