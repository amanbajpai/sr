package com.ros.smartrocket.db.entity;

public class SetPassword extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    private String Email;
    private String PasswordResetToken;
    private String NewPassword;

    public String getPasswordResetToken() {
        return PasswordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        PasswordResetToken = passwordResetToken;
    }

    public String getNewPassword() {
        return NewPassword;
    }

    public void setNewPassword(String newPassword) {
        NewPassword = newPassword;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
