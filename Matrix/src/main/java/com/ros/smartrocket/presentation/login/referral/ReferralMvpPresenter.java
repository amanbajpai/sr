package com.ros.smartrocket.presentation.login.referral;

import com.ros.smartrocket.presentation.base.MvpPresenter;

interface ReferralMvpPresenter<V extends ReferralMvpView> extends MvpPresenter<V> {
    void getReferralCases(int countryId);

    void saveReferralCases(int countryId, int referralCaseId);
}
