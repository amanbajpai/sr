package com.ros.smartrocket.ui.login.referral;

import com.ros.smartrocket.db.entity.ReferralCases;
import com.ros.smartrocket.ui.base.NetworkMvpView;

interface ReferralMvpView extends NetworkMvpView {

    void onReferralCasesLoaded(ReferralCases cases);

    void onReferralCasesSaved();
}
