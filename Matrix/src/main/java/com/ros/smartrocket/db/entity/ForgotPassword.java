package com.ros.smartrocket.db.entity;

public class ForgotPassword extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Email;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }


}
