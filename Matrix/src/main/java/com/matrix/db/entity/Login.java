package com.matrix.db.entity;

public class Login extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Mail;
    private String Password;

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }


}
