package com.ros.smartrocket.net.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ros.smartrocket.utils.PreferencesManager;

/**
 * Created by ankurrawal on 10/8/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIDService";
    private PreferencesManager preferencesManager = PreferencesManager.getInstance();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        if (refreshedToken != null) {
            Log.d(TAG, "Refreshed token: " + refreshedToken);
            preferencesManager.setFirebaseToken(refreshedToken);

            String testToken = PreferencesManager.getInstance().getFirebaseToken();
            Log.e("Token by firebase", testToken);
        } else {
            refreshedToken = FirebaseInstanceId.getInstance().getToken();
            preferencesManager.setFirebaseToken(refreshedToken);
        }
    }


}