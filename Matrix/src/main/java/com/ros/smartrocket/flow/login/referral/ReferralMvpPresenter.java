package com.ros.smartrocket.flow.login.referral;

import com.ros.smartrocket.flow.base.MvpPresenter;

interface ReferralMvpPresenter<V extends ReferralMvpView> extends MvpPresenter<V> {
    void getReferralCases(int countryId);

    void saveReferralCases(int countryId, int referralCaseId);
}
