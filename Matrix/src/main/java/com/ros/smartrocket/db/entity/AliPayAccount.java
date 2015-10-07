package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by macbook on 06.10.15.
 */
public class AliPayAccount extends BaseEntity {

    @SerializedName("AliPayAccountName")
    private String accName;
    @SerializedName("AliPayPhoneNumber")
    private String phone;
    @SerializedName("SmsCode")
    private String smsCode;


    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
