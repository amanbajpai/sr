package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class UpdateUser extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("PhotoBase64")
    private String photoBase64;

    @SerializedName("FirstName")
    private String firstName;

    @SerializedName("LastName")
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }


}
