package com.matrix.db.entity;

public class Registration extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Email;
    private String Password;
    private String FirstName;
    private String LastName;
    private Integer Gender;
    private String Birthday;
    private Integer CountryId;
    private Integer CityId;
    private Double Longitude;
    private Double Latitude;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public Integer getGender() {
        return Gender;
    }

    public void setGender(Integer gender) {
        Gender = gender;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
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

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }
}
