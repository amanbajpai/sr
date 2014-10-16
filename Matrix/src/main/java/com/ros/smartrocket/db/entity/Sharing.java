package com.ros.smartrocket.db.entity;

public class Sharing extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Text;
    private String Link;

    private Integer BitMaskSocialNetwork;

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public Integer getBitMaskSocialNetwork() {
        return BitMaskSocialNetwork;
    }

    public void setBitMaskSocialNetwork(Integer bitMaskSocialNetwork) {
        BitMaskSocialNetwork = bitMaskSocialNetwork;
    }

}
