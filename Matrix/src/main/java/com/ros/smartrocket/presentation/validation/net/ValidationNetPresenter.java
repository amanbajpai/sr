package com.ros.smartrocket.presentation.validation.net;

import android.location.Location;
import android.location.LocationManager;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.account.Token;
import com.ros.smartrocket.db.entity.question.Answer;
import com.ros.smartrocket.db.entity.task.Task;
import com.ros.smartrocket.map.location.MatrixLocationManager;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.SendTaskIdMapper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ValidationNetPresenter<V extends ValidationNetMvpView> extends BaseNetworkPresenter<V> implements ValidationNetMvpPresenter<V> {
    @Override
    public void validateTask(Task task) {
        showLoading(false);
        Location location = new Location(LocationManager.NETWORK_PROVIDER);
        location.setLatitude(task.getLatitudeToValidation());
        location.setLongitude(task.getLongitudeToValidation());
        MatrixLocationManager
                .getAddressByLocation(location, (location1, countryName, cityName, districtName) -> validateTaskRequest(task, cityName, location));
    }

//    private Location location;
//
//    private void findLocation() {
//        MatrixLocationManager.getCurrentLocation(false, new CurrentLocationListener() {
//            @Override
//            public void getLocationSuccess(Location location) {
//                if (!isViewAttached()) return;
//                ValidationNetPresenter.this.location = location;
//            }
//
//            @Override
//            public void getLocationFail(String errorText) {
//                if (!isViewAttached()) return;
//                hideLoading();
//                UIUtils.showSimpleToast(App.getInstance(), errorText);
//            }
//        });
//    }

    private void validateTaskRequest(Task task, String cityName, Location location) {
        if (isViewAttached())
            addDisposable(App.getInstance().getApi()
                    .validateTask(SendTaskIdMapper.getSendTaskIdForValidation(task, cityName, location))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(__ -> onTaskOnValidation(), this::showNetworkError));
    }

    private void onTaskOnValidation() {
        hideLoading();
        getMvpView().taskOnValidation();
    }

    @Override
    public void getNewToken() {
        Token token = new Token();
        token.setToken(PreferencesManager.getInstance().getToken());
        addDisposable(App.getInstance().getApi()
                .getNewToken(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNewTokenRetrieved, this::showNetworkError));
    }

    private void onNewTokenRetrieved(Token token) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        preferencesManager.setToken(token.getToken());
        preferencesManager.setTokenForUploadFile(token.getToken());
        preferencesManager.setTokenUpdateDate(System.currentTimeMillis());
        getMvpView().onNewTokenRetrieved();
    }

    @Override
    public void startTask(Task task) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .startTask(SendTaskIdMapper.getSendTaskIdForStart(task))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onTaskStarted(), this::showNetworkError));
    }

    private void onTaskStarted() {
        hideLoading();
        getMvpView().onTaskStarted();
    }

    @Override
    public void sendAnswers(List<Answer> answers, Integer missionId) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .sendAnswers(answers, missionId, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> onAnswersSent(), __ -> onAnswersNotSent()));
    }

    private void onAnswersSent() {
        hideLoading();
        getMvpView().onAnswersSent();
    }

    private void onAnswersNotSent() {
        getMvpView().onAnswersSent();
    }
}
