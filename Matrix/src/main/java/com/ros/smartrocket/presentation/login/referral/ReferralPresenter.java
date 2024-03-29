package com.ros.smartrocket.presentation.login.referral;

import android.support.annotation.NonNull;

import com.ros.smartrocket.App;
import com.ros.smartrocket.db.entity.account.register.ReferralCases;
import com.ros.smartrocket.db.entity.account.register.SaveReferralCase;
import com.ros.smartrocket.presentation.base.BaseNetworkPresenter;
import com.ros.smartrocket.utils.PreferencesManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class ReferralPresenter<V extends ReferralMvpView> extends BaseNetworkPresenter<V> implements ReferralMvpPresenter<V> {

    @Override
    public void getReferralCases(int countryId) {
        showLoading(false);
        addDisposable(App.getInstance().getApi()
                .getReferralCases(countryId, getLanguageCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleLoadedRK, this::showNetworkError));
    }

    @Override
    public void saveReferralCases(int countryId, int referralCaseId) {
        if (PreferencesManager.getInstance().getToken().isEmpty()) {
            getMvpView().continueWithoutSendCases();
        } else {
            showLoading(false);
            addDisposable(App.getInstance().getApi()
                    .saveReferralCases(getReferralCase(countryId, referralCaseId))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(r -> handleSaveRK(), this::showNetworkError)
            );
        }
    }

    private void handleSaveRK() {
        hideLoading();
        getMvpView().onReferralCasesSaved();
    }

    private void handleLoadedRK(ReferralCases rk) {
        hideLoading();
        getMvpView().onReferralCasesLoaded(rk);
    }

    @NonNull
    private SaveReferralCase getReferralCase(int countryId, int referralCaseId) {
        SaveReferralCase caseEntity = new SaveReferralCase();
        caseEntity.setCountryId(countryId);
        caseEntity.setReferralId(referralCaseId);
        return caseEntity;
    }
}
