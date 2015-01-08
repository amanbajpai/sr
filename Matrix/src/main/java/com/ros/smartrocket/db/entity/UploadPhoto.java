package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class UploadPhoto extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("PhotoBase64")
    private String photoBase64;

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }


}
