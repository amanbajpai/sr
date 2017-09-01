package com.ros.smartrocket.ui.login.location;

import android.location.Location;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.CheckLocation;
import com.ros.smartrocket.db.entity.CheckLocationResponse;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.ui.base.BasePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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
        addDisposable(App.getInstance().getApi()
                .checkLocationForRegistration(checkLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        r -> getMvpView().onLocationChecked(r, checkLocation.getLatitude(), checkLocation.getLongitude()),
                        throwable -> getMvpView().onLocationCheckFailed()));
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
