package com.ros.smartrocket.interfaces;

import com.ros.smartrocket.db.entity.ExternalAuthorize;

public interface SocialLoginListener {
    void onExternalLoginSuccess(ExternalAuthorize authorize);

    void onExternalLoginStart();

    void onExternalLoginFinished();

}
