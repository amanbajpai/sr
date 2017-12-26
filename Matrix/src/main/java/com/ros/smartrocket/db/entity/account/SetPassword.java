package com.ros.smartrocket.db.entity.account;

import com.google.gson.annotations.SerializedName;
import com.ros.smartrocket.db.entity.BaseEntity;

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
