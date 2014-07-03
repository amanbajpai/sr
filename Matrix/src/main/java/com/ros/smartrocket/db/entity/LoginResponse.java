package com.ros.smartrocket.db.entity;

public class LoginResponse extends BaseEntity {
    private static final long serialVersionUID = -3693887084641009133L;

    private String Token;
    private String SharedLink;
    private Integer BitMaskSocialNetwork;
    private Boolean State = false;

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }

    public Boolean getState() {
        return State;
    }

    public void setState(Boolean state) {
        this.State = state;
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
