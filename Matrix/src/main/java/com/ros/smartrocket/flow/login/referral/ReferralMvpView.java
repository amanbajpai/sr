package com.ros.smartrocket.flow.login.referral;

import com.ros.smartrocket.db.entity.ReferralCases;
import com.ros.smartrocket.flow.base.NetworkMvpView;

interface ReferralMvpView extends NetworkMvpView {

    void onReferralCasesLoaded(ReferralCases cases);

    void onReferralCasesSaved();
}
