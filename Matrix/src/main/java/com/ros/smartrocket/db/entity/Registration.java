package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Registration extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Email")
    private String email;
    @SerializedName("Password")
    private String password;
    @SerializedName("SingleName")
    private String fullName;
    @SerializedName("Gender")
    private Integer gender;
    @SerializedName("ReferralId")
    private Integer referralId;
    @SerializedName("Birthday")
    private String birthday;
    @SerializedName("DistrictId")
    private Integer districtId;
    @SerializedName("CountryId")
    private Integer countryId;
    @SerializedName("CityId")
    private Integer cityId;
    @SerializedName("Longitude")
    private Double longitude;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("GroupCode")
    private String groupCode;
    @SerializedName("TermsAndConditionsVersion")
    private Integer termsAndConditionsVersion;
    @SerializedName("PhotoBase64")
    private String photoBase64;
    @SerializedName("PromoCode")
    private String promoCode;
    @SerializedName("IsViewTermsAndConditions")
    private boolean isTermsShowed;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }


    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public Integer getTermsAndConditionsVersion() {
        return termsAndConditionsVersion;
    }

    public void setTermsAndConditionsVersion(Integer termsAndConditionsVersion) {
        this.termsAndConditionsVersion = termsAndConditionsVersion;
    }

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }


    public Integer getReferralId() {
        return referralId;
    }

    public void setReferralId(Integer referralId) {
        this.referralId = referralId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isTermsShowed() {
        return isTermsShowed;
    }

    public void setTermsShowed(boolean termsShowed) {
        isTermsShowed = termsShowed;
    }
}
