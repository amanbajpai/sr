package com.ros.smartrocket.db.entity;

public class ReferralCases extends BaseEntity {
    private static final long serialVersionUID = 5410835468659163958L;

    private ReferralCase[] Cases;

    public ReferralCases() {
    }

    public ReferralCase[] getCases() {
        return Cases;
    }

    public void setCases(ReferralCase[] cases) {
        Cases = cases;
    }

}
