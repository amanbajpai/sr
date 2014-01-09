package com.ros.smartrocket.bl;

import android.content.Context;
import android.text.TextUtils;
import com.ros.smartrocket.utils.UIUtils;

/**
 *
 */
public class LoginBL {
    //private static final String TAG = LoginBL.class.getSimpleName();

    /**
     * All possible errors before login initiation
     */
    public enum PreLoginErrors {
        SUCCESS,
        NOCONNECTION,
        GPSOFF,
        GOOGLEPSNOTWALID,
        MOCKON,
        NOPASSWORDOREMAIL;
    }

    public static UIUtils utils;

    /**
     * Check all conditions before login
     * @param context
     * @param email
     * @param password
     * @return
     */
    public PreLoginErrors login(Context context, String email, String password) {
        PreLoginErrors errors = PreLoginErrors.SUCCESS;

        if (!utils.isOnline(context)) {
            errors = PreLoginErrors.NOCONNECTION;
        } else if (!utils.isGpsEnabled(context)) {
            errors = PreLoginErrors.GPSOFF;
        } else if (!utils.isGooglePlayServicesEnabled(context)) {
            errors = PreLoginErrors.GOOGLEPSNOTWALID;
        } else if (utils.isMockLocationEnabled(context)) {
            errors = PreLoginErrors.MOCKON;
        } else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            errors = PreLoginErrors.NOPASSWORDOREMAIL;
        }
        return errors;
    }
}
