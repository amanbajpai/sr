package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class WeChatUserInfoResponse extends BaseEntity {
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("sex")
    private int sex;
    @SerializedName("city")
    private String city;
    @SerializedName("country")
    private String country;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
