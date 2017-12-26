package com.ros.smartrocket.db.entity.account;

import com.google.gson.annotations.SerializedName;

public class ExternalAuthorize extends Login {
    @SerializedName("SingleName")
    private String fullName;
    @SerializedName("Gender")
    private Integer gender;
    @SerializedName("ExternalAuthSource")
    private Integer externalAuthSource;
    @SerializedName("ExternalAuthToken")
    private String externalAuthToken;
    @SerializedName("Birthday")
    private String birthday;
    @SerializedName("ReferralId")
    private Integer referralId;
    @SerializedName("PromoCode")
    private String promoCode;
    @SerializedName("AdditionalAuthClaims")
    private AdditionalAuthClaim additionalAuthClaims;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getExternalAuthSource() {
        return externalAuthSource;
    }

    public void setExternalAuthSource(Integer externalAuthSource) {
        this.externalAuthSource = externalAuthSource;
    }

    public String getExternalAuthToken() {
        return externalAuthToken;
    }

    public void setExternalAuthToken(String externalAuthToken) {
        this.externalAuthToken = externalAuthToken;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getReferralId() {
        return referralId;
    }

    public void setReferralId(Integer referralId) {
        this.referralId = referralId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public AdditionalAuthClaim getAdditionalAuthClaims() {
        return additionalAuthClaims;
    }

    public void setAdditionalAuthClaims(AdditionalAuthClaim additionalAuthClaims) {
        this.additionalAuthClaims = additionalAuthClaims;
    }
}
