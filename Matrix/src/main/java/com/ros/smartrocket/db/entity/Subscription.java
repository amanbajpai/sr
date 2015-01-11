package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Subscription extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Country")
    private String country;
    @SerializedName("City")
    private String city;
    @SerializedName("Email")
    private String email;
    @SerializedName("Longitude")
    private Double longitude;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("DistrictId")
    private Integer districtId;
    @SerializedName("CountryId")
    private Integer countryId;
    @SerializedName("CityId")
    private Integer cityId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Integer getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Integer districtId) {
        this.districtId = districtId;
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
}
