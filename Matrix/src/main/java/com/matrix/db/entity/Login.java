package com.matrix.db.entity;

public class Login extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Email;
    private String Password;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


}
