package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by macbook on 06.10.15.
 */
public class AliPayAccount extends BaseEntity {

    @SerializedName("AliPayAccountName")
    private String accName;
    @SerializedName("AlipayUserId")
    private String userId;
    @SerializedName("SmsCode")
    private String smsCode;

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
