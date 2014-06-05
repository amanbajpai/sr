package com.ros.smartrocket.db.entity;

public class UploadPhoto extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String PhotoBase64;

    public String getPhotoBase64() {
        return PhotoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.PhotoBase64 = photoBase64;
    }


}
