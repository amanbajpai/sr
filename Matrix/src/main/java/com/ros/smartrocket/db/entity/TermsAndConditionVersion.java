package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class TermsAndConditionVersion extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    @SerializedName("Version")
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


}
