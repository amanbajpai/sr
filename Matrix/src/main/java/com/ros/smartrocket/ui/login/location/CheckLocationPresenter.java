package com.ros.smartrocket.ui.login.location;

import android.location.Location;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.CheckLocation;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.ui.base.BasePresenter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckLocationPresenter<V extends CheckLocationMvpView> extends BasePresenter<V>
        implements CheckLocationMvpPresenter<V> {


    @Override
    public void checkLocation() {
        getLocation();
    }

    private void getLocation() {
        getMvpView().showLocationCheckDialog();
        MatrixLocationManager.getAddressByCurrentLocation(false, (location, countryName, cityName, districtName)
                -> checkLocationForRegistration(getCheckLocationEntity(location, countryName, cityName, districtName)));
    }

    private void checkLocationForRegistration(CheckLocation checkLocation) {
        Call<CheckLocationResponse> call = App.getInstance().getApi().checkLocationForRegistration(checkLocation);
        call.enqueue(new Callback<CheckLocationResponse>() {
            @Override
            public void onResponse(Call<CheckLocationResponse> call, Response<CheckLocationResponse> response) {
                if (response.isSuccessful()) {
                    getMvpView().onLocationChecked(response.body(), checkLocation.getLatitude(), checkLocation.getLongitude());
                } else {
                    getMvpView().onLocationCheckFailed();
                }
            }

            @Override
            public void onFailure(Call<CheckLocationResponse> call, Throwable t) {
                getMvpView().onLocationCheckFailed();
            }
        });
    }

    private CheckLocation getCheckLocationEntity(Location location, String countryName, String cityName, String districtName) {
        CheckLocation checkLocationEntity = new CheckLocation();
        checkLocationEntity.setCountry(countryName);
        checkLocationEntity.setCity(cityName);
        checkLocationEntity.setDistrict(districtName);
        checkLocationEntity.setLatitude(location.getLatitude());
        checkLocationEntity.setLongitude(location.getLongitude());
        return checkLocationEntity;
    }


}
