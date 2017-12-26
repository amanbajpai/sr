package com.ros.smartrocket.db.entity.location;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;
import com.ros.smartrocket.db.entity.account.register.RegistrationPermissions;

public class CheckLocationResponse extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Status")
    private Boolean status = false;
    @SerializedName("CountryId")
    private Integer countryId;
    @SerializedName("CityId")
    private Integer cityId;
    @SerializedName("DistrictId")
    private Integer districtId;
    @SerializedName("CountryName")
    private String countryName;
    @SerializedName("CityName")
    private String cityName;
    @SerializedName("RegistrationTypes")
    private int registrationTypes;
    @SerializedName("ExternalLoginSource1")
    private int externalLoginSource1;
    @SerializedName("ExternalLoginSource2")
    private int externalLoginSource2;


    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
    }


    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getRegistrationTypes() {
        return registrationTypes;
    }

    public void setRegistrationTypes(int registrationTypes) {
        this.registrationTypes = registrationTypes;
    }

    public RegistrationPermissions getRegistrationPermissions() {
        return new RegistrationPermissions(registrationTypes);
    }

    public int getExternalLoginSource1() {
        return externalLoginSource1;
    }

    public void setExternalLoginSource1(int externalLoginSource1) {
        this.externalLoginSource1 = externalLoginSource1;
    }

    public int getExternalLoginSource2() {
        return externalLoginSource2;
    }

    public void setExternalLoginSource2(int externalLoginSource2) {
        this.externalLoginSource2 = externalLoginSource2;
    }
}
