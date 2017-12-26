package com.ros.smartrocket.db.entity.account;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

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
