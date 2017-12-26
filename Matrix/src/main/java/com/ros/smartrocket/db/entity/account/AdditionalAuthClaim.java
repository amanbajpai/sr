package com.ros.smartrocket.db.entity.account;

import java.io.Serializable;

public class AdditionalAuthClaim implements Serializable {
    private static final long serialVersionUID = 2857267598118484900L;
    private String openid;

    public AdditionalAuthClaim(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}
