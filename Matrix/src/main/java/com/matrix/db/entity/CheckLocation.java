package com.matrix.db.entity;

public class CheckLocation extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Country;
    private String City;

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }


}
