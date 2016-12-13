package com.ros.smartrocket.db.entity;

import java.io.Serializable;

public class RegistrationPermissions implements Serializable {

    private static final int SLIDERS = 1;
    private static final int SOCIAL = 2;
    private static final int TERMS = 3;
    private static final int REFERRAL = 4;
    private static final int SR_CODE = 5;

    private boolean isReferralEnable = false;
    private boolean isSocialEnable = false;
    private boolean isSlidersEnable = false;
    private boolean isTermsEnable = false;
    private boolean isSrCodeEnable = false;

    public RegistrationPermissions(int[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
                case SLIDERS:
                    isSlidersEnable = true;
                    break;
                case SOCIAL:
                    isSocialEnable = true;
                    break;
                case TERMS:
                    isTermsEnable = true;
                    break;
                case REFERRAL:
                    isReferralEnable = true;
                    break;
                case SR_CODE:
                    isSrCodeEnable = true;
                    break;
            }
        }

    }

    public boolean isReferralEnable() {
        return isReferralEnable;
    }

    public boolean isSocialEnable() {
        return isSocialEnable;
    }

    public boolean isSlidersEnable() {
        return isSlidersEnable;
    }

    public boolean isTermsEnable() {
        return isTermsEnable;
    }

    public boolean isSrCodeEnable() {
        return isSrCodeEnable;
    }
}
