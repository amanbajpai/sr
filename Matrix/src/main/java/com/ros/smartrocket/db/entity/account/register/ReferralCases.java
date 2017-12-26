package com.ros.smartrocket.db.entity.account.register;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class ReferralCases extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    @SerializedName("Cases")
    private ReferralCase[] cases;

    public ReferralCases() {
    }

    public ReferralCase[] getCases() {
        return cases;
    }

}
