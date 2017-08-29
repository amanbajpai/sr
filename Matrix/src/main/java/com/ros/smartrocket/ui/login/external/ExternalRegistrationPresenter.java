package com.ros.smartrocket.ui.login.external;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.ExternalAuthorize;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.base.BasePresenter;
import com.ros.smartrocket.utils.PreferencesManager;
import com.ros.smartrocket.utils.UIUtils;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ExternalRegistrationPresenter<V extends ExternalRegistrationMvpView> extends BasePresenter<V> implements ExternalRegistrationMvpPresenter<V> {
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

        getMvpView().showLoading(false);
        Call<ResponseBody> call = App.getInstance().getApi()
                .externalRegistration(externalAuthorize, PreferencesManager.getInstance().getLanguageCode());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    if (response.isSuccessful())
                        getMvpView().onRegistrationSuccess(email);
                    else
                        getMvpView().showNetworkError(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    getMvpView().showNetworkError(new NetworkError(t));
                }
            }
        });
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
