package com.ros.smartrocket.flow.login.external;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.flow.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.UIUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ExternalRegistrationPresenter<V extends ExternalRegistrationMvpView> extends BaseNetworkPresenter<V> implements ExternalRegistrationMvpPresenter<V> {
    private static final int EMAIL_MASK = 1;
    private static final int BIRTH_MASK = 2;

    private int bitMasc;
    private ExternalAuthorize externalAuthorize;


    ExternalRegistrationPresenter(int bitMasc, ExternalAuthorize externalAuthorize) {
        this.bitMasc = bitMasc;
        this.externalAuthorize = externalAuthorize;
        setUpUI();
    }

    private void setUpUI() {
        if (isBirthdayNeeded()) getMvpView().showDoBField();
        if (isEmailNeeded()) getMvpView().showEmailField();
    }

    @Override
    public void registerExternal(Long dob, String email) {
        if (isAllFieldsFilled(dob, email))
            registerUser(dob, email);
        else
            getMvpView().onFieldsEmpty();
    }

    private void registerUser(Long dob, String email) {
        if (dob != null)
            externalAuthorize.setBirthday(UIUtils.longToString(dob, 2));
        if (!email.isEmpty())
            externalAuthorize.setEmail(email);

        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .externalRegistration(externalAuthorize, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(r -> handleRegistration(email), this::showNetworkError)

        );
    }

    private void handleRegistration(String email) {
        hideLoading();
        getMvpView().onRegistrationSuccess(email);
    }

    private boolean isEmailNeeded() {
        return (bitMasc & EMAIL_MASK) == EMAIL_MASK;
    }

    private boolean isBirthdayNeeded() {
        return (bitMasc & BIRTH_MASK) == BIRTH_MASK;
    }

    private boolean isAllFieldsFilled(Long selectedBirthDay, String email) {
        boolean result = !isEmailNeeded() || !email.isEmpty();
        result &= !isBirthdayNeeded() || selectedBirthDay != null;
        return result;
    }
}
