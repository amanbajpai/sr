package com.ros.smartrocket.db.entity;

public class Sharing extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String SharedText;
    private String SharedLink;
    private Integer BitMaskSocialNetwork;

    public String getSharedText() {
        return SharedText;
    }

    public void setSharedText(String sharedText) {
        SharedText = sharedText;
    }

    public String getSharedLink() {
        return SharedLink;
    }

    public void setSharedLink(String sharedLink) {
        SharedLink = sharedLink;
    }

    public Integer getBitMaskSocialNetwork() {
        return BitMaskSocialNetwork;
    }

    public void setBitMaskSocialNetwork(Integer bitMaskSocialNetwork) {
        BitMaskSocialNetwork = bitMaskSocialNetwork;
    }

}
