package com.ros.smartrocket.db.entity.account;

import com.ros.smartrocket.db.entity.BaseEntity;

public class NationalIdAccount extends BaseEntity {

    private String name;
    private String nationalId;
    private String phoneNumber;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
