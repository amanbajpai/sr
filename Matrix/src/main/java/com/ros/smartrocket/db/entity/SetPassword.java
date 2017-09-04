package com.ros.smartrocket.db.entity;

import com.google.gson.annotations.SerializedName;

public class SetPassword extends BaseEntity {
    private static final long serialVersionUID = 2857267798118484900L;

    @SerializedName("Email")
    private String email;
    @SerializedName("PasswordResetToken")
    private String passwordResetToken;
    @SerializedName("NewPassword")
    private String newPassword;

    public SetPassword(String email, String passwordResetToken, String newPassword) {
        this.email = email;
        this.passwordResetToken = passwordResetToken;
        this.newPassword = newPassword;
    }
}
