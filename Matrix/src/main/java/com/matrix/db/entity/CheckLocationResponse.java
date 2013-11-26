package com.matrix.db.entity;

public class CheckLocationResponse extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private Boolean State = false;
    private Integer CountryId;
    private Integer CityId;

    public Boolean getState() {
        return State;
    }

    public void setState(Boolean state) {
        this.State = state;
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

}
