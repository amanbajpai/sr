package com.ros.smartrocket.db.entity.account.register;

import com.ros.smartrocket.db.entity.BaseEntity;

public class ReferralCase extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    private String Case;

    public String getCase() {
        return Case;
    }

    public void setCase(String aCase) {
        Case = aCase;
    }


}
