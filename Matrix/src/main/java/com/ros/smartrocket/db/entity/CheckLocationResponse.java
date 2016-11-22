package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

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
    private int[] registrationTypes;


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

    public int[] getRegistrationTypes() {
        return registrationTypes;
    }

    public void setRegistrationTypes(int[] registrationTypes) {
        this.registrationTypes = registrationTypes;
    }

    public RegistrationPermissions getRegistrationPermissions() {
        return registrationTypes != null ? new RegistrationPermissions(registrationTypes) : null;
    }
}
