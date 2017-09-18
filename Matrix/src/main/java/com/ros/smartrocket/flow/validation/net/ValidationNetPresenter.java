package com.ros.smartrocket.flow.validation.net;

import android.location.Location;
import android.location.LocationManager;

import com.ros.smartrocket.db.entity.Answer;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;
import com.ros.smartrocket.map.location.MatrixLocationManager;

import java.util.List;

public class ValidationNetPresenter<V extends ValidationNetMvpView> extends BaseNetworkPresenter<V> implements ValidationNetMvpPresenter<V> {
    @Override
    public void validateTask(Task task) {
        showLoading(false);
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(task.getLatitudeToValidation());
        location.setLongitude(task.getLongitudeToValidation());
        MatrixLocationManager
                .getAddressByLocation(location, (location1, countryName, cityName, districtName) -> validateTaskRequest(task, cityName));
    }

    private void validateTaskRequest(Task task, String cityName) {

        //sendNetworkOperation(apiFacade.getValidateTaskOperation(task.getWaveId(), task.getId(), task.getMissionId(), task.getLatitudeToValidation(), task.getLongitudeToValidation(), cityName))
    }

    @Override
    public void getNewToken() {

    }

    @Override
    public void startTask(Task task) {
        showLoading(false);
    }

    @Override
    public void sendAnswers(List<Answer> answers, Integer missionId) {
        showLoading(false);
    }
}
