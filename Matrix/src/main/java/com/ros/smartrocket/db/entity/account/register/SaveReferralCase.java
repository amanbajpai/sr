package com.ros.smartrocket.db.entity.account.register;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class SaveReferralCase extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("CountryId")
    private Integer countryId;
    @SerializedName("ReferralId")
    private Integer referralId;

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getReferralId() {
        return referralId;
    }

    public void setReferralId(Integer referralId) {
        this.referralId = referralId;
    }


}
