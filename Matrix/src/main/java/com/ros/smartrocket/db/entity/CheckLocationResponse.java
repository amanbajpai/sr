package com.ros.smartrocket.db.entity;

public class CheckLocationResponse extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private Boolean Status = false;
    private Integer CountryId;
    private Integer CityId;
    private Integer DistrictId;

    public Boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        this.Status = status;
    }

    public Integer getCountryId() {
        return CountryId;
    }

    public void setCountryId(Integer countryId) {
        CountryId = countryId;
    }

    public Integer getCityId() {
        return CityId;
    }

    public void setCityId(Integer cityId) {
        CityId = cityId;
    }

    public Integer getDistrictId() {
        return DistrictId;
    }

    public void setDistrictId(Integer districtId) {
        DistrictId = districtId;
    }

}
