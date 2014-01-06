package com.ros.smartrocket.bl;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import com.ros.smartrocket.App;
import com.ros.smartrocket.db.TaskDbSchema;
import com.ros.smartrocket.db.entity.Task;
import com.ros.smartrocket.utils.DialogUtils;
import com.ros.smartrocket.utils.UIUtils;

import java.util.ArrayList;

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

    /**
     * Check all conditions before login
     * @param context
     * @param email
     * @param password
     * @return
     */
    public PreLoginErrors login(Context context, String email, String password) {
        PreLoginErrors errors = PreLoginErrors.SUCCESS;
        if (!UIUtils.isOnline(context)) {
            errors = PreLoginErrors.NOCONNECTION;
        } else if (!UIUtils.isGpsEnabled(context)) {
            errors = PreLoginErrors.GPSOFF;
        } else if (!UIUtils.isGooglePlayServicesEnabled(context)) {
            errors = PreLoginErrors.GOOGLEPSNOTWALID;
        } else if (UIUtils.isMockLocationEnabled(context)) {
            errors = PreLoginErrors.MOCKON;
        } else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            errors = PreLoginErrors.NOPASSWORDOREMAIL;
        }
        return errors;
    }
}
