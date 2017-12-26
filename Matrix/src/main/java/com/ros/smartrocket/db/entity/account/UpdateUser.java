package com.ros.smartrocket.db.entity.account;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

public class UpdateUser extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("PhotoBase64")
    private String photoBase64;

    @SerializedName("SingleName")
    private String singleName;

    public String getSingleName() {
        return singleName;
    }

    public void setSingleName(String singleName) {
        this.singleName = singleName;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }


}
