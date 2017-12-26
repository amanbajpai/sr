package com.ros.smartrocket.presentation.login.referral;

import com.ros.smartrocket.db.entity.account.register.ReferralCases;
import com.ros.smartrocket.presentation.base.NetworkMvpView;

interface ReferralMvpView extends NetworkMvpView {

    void onReferralCasesLoaded(ReferralCases cases);

    void onReferralCasesSaved();

    void continueWithoutSendCases();
}
