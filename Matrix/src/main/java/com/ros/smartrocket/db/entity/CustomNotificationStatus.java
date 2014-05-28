package com.ros.smartrocket.db.entity;

public class CustomNotificationStatus extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private boolean hiden = false;

    public boolean isHiden() {
        return hiden;
    }

    public void setHiden(boolean hiden) {
        this.hiden = hiden;
    }
}
