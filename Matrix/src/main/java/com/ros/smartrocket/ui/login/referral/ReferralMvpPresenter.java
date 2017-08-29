package com.ros.smartrocket.ui.login.referral;

import com.ros.smartrocket.ui.base.MvpPresenter;

interface ReferralMvpPresenter<V extends ReferralMvpView> extends MvpPresenter<V> {
    void getReferralCases(int countryId);

    void saveReferralCases(int countryId, int referralCaseId);
}
