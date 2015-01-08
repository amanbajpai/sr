package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class Sharing extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("SharedText")
    private String sharedText;
    @SerializedName("SharedLink")
    private String sharedLink;
    @SerializedName("BitMaskSocialNetwork")
    private Integer bitMaskSocialNetwork;

    public String getSharedText() {
        return sharedText;
    }

    public void setSharedText(String sharedText) {
        this.sharedText = sharedText;
    }

    public String getSharedLink() {
        return sharedLink;
    }

    public void setSharedLink(String sharedLink) {
        this.sharedLink = sharedLink;
    }

    public Integer getBitMaskSocialNetwork() {
        return bitMaskSocialNetwork;
    }

    public void setBitMaskSocialNetwork(Integer bitMaskSocialNetwork) {
        this.bitMaskSocialNetwork = bitMaskSocialNetwork;
    }

}
