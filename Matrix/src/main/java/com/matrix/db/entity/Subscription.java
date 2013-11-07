package com.matrix.db.entity;

public class Subscription extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Country;
    private String City;
    private String Mail;

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

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
