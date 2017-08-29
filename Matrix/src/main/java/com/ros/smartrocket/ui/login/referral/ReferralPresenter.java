package com.ros.smartrocket.ui.login.referral;

import android.support.annotation.NonNull;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.ReferralCases;
import com.ros.smartrocket.db.entity.SaveReferralCase;
import com.ros.smartrocket.net.NetworkError;
import com.ros.smartrocket.ui.base.BasePresenter;
import com.ros.smartrocket.utils.PreferencesManager;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class ReferralPresenter<V extends ReferralMvpView> extends BasePresenter<V> implements ReferralMvpPresenter<V> {

    @Override
    public void getReferralCases(int countryId) {
        getMvpView().showLoading(false);
        Call<ReferralCases> call = App.getInstance().getApi()
                .getReferralCases(countryId, PreferencesManager.getInstance().getLanguageCode());
        call.enqueue(new Callback<ReferralCases>() {
            @Override
            public void onResponse(Call<ReferralCases> call, Response<ReferralCases> response) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    if (response.isSuccessful())
                        getMvpView().onReferralCasesLoaded(response.body());
                    else
                        getMvpView().showNetworkError(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ReferralCases> call, Throwable t) {
                onError(t);
            }
        });
    }

    @Override
    public void saveReferralCases(int countryId, int referralCaseId) {
        getMvpView().showLoading(false);
        Call<ResponseBody> call = App.getInstance().getApi().saveReferralCases(getReferralCase(countryId, referralCaseId));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                    if (response.isSuccessful())
                        getMvpView().onReferralCasesSaved();
                    else
                        getMvpView().showNetworkError(new NetworkError(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onError(t);
            }
        });
    }

    @NonNull
    private SaveReferralCase getReferralCase(int countryId, int referralCaseId) {
        SaveReferralCase caseEntity = new SaveReferralCase();
        caseEntity.setCountryId(countryId);
        caseEntity.setReferralId(referralCaseId);
        return caseEntity;
    }

    private void onError(Throwable t) {
        if (isViewAttached()) {
            getMvpView().hideLoading();
            getMvpView().showNetworkError(new NetworkError(t));
        }
    }
}
