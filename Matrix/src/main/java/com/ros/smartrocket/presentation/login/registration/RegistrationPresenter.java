package com.ros.smartrocket.presentation.login.registration;

import android.text.TextUtils;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.Registration;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.UIUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class RegistrationPresenter<V extends RegistrationMvpView> extends BaseNetworkPresenter<V> implements RegistrationMvpPresenter<V> {

    @Override
    public void register(Registration registration) {
        if (isRegistrationValid(registration))
            registerUser(registration);
    }

    private void registerUser(Registration registration) {
        getMvpView().showLoading(false);
        addDisposable(App.getInstance().getApi()
                .registration(registration, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> handleSuccess(), this::showNetworkError));
    }

    private void handleSuccess() {
        getMvpView().hideLoading();
        getMvpView().onRegistrationSuccess();
    }

    private boolean isRegistrationValid(Registration registration) {
        return isNameValid(registration.getFullName())
                && isGenderValid(registration.getGender())
                && isBirthDayValid(registration.getBirthday())
                && isEmailValid(registration.getEmail())
                && isPasswordValid(registration.getPassword());
    }

    private boolean isPasswordValid(String password) {
        if (!UIUtils.isPasswordValid(password)) {
            getMvpView().notValidPassword();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        if (!UIUtils.isEmailValid(email)) {
            getMvpView().notValidEmail();
            return false;
        }
        return true;
    }

    private boolean isNameValid(String name) {
        if (TextUtils.isEmpty(name)) {
            getMvpView().notValidName();
            return false;
        }
        return true;
    }

    private boolean isBirthDayValid(String birthDay) {
        if (TextUtils.isEmpty(birthDay)) {
            getMvpView().notValidBirthday();
            return false;
        }
        return true;
    }

    private boolean isGenderValid(int gender) {
        if (gender == -1) {
            getMvpView().notValidGender();
            return false;
        }
        return true;
    }
}
