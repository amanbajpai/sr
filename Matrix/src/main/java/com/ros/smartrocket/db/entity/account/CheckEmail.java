package com.ros.smartrocket.db.entity.account;

import com.ros.smartrocket.db.entity.BaseEntity;

public class CheckEmail extends BaseEntity {

    private boolean emailExists;

    public boolean isEmailExists() {
        return emailExists;
    }

    public void setEmailExists(boolean emailExists) {
        this.emailExists = emailExists;
    }
}
