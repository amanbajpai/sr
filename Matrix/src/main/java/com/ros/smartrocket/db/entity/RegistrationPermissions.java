package com.ros.smartrocket.db.entity;

import java.io.Serializable;

public class RegistrationPermissions implements Serializable {

    public static final int ALL = 31;
    private static final int SLIDERS = 1;
    private static final int SOCIAL = 2;
    private static final int TERMS = 4;
    private static final int REFERRAL = 8;
    private static final int SR_CODE = 16;
    private int mask;

    public RegistrationPermissions(int mask) {
        this.mask = mask;
    }

    public boolean isReferralEnable() {
        return (mask & REFERRAL) == REFERRAL;
    }

    public boolean isSocialEnable() {
        return (mask & SOCIAL) == SOCIAL;
    }

    public boolean isSlidersEnable() {
        return (mask & SLIDERS) == SLIDERS;
    }

    public boolean isTermsEnable() {
        return (mask & TERMS) == TERMS;
    }

    public boolean isSrCodeEnable() {
        return (mask & SR_CODE) == SR_CODE;
    }
}
