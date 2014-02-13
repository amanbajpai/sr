package com.ros.smartrocket.db.entity;

public class SaveReferralCase extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    private Integer CountryId;
    private Integer ReferralId;

    public Integer getCountryId() {
        return CountryId;
    }

    public void setCountryId(Integer countryId) {
        CountryId = countryId;
    }

    public Integer getReferralId() {
        return ReferralId;
    }

    public void setReferralId(Integer referralId) {
        ReferralId = referralId;
    }


}
