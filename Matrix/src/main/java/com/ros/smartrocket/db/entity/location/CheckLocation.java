package com.ros.smartrocket.db.entity.location;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class CheckLocation extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Country")
    private String country;
    @SerializedName("City")
    private String city;
    @SerializedName("District")
    private String district;
    @SerializedName("Latitude")
    private Double latitude;
    @SerializedName("Longitude")
    private Double longitude;

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

}
