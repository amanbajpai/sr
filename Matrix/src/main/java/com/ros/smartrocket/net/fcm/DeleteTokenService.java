package com.ros.smartrocket.net.fcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ros.smartrocket.utils.PreferencesManager;

/**
 * Service used to refresh device token of user.
 */

@SuppressWarnings("ALL")
public class DeleteTokenService extends IntentService {
    public static final String TAG = DeleteTokenService.class.getSimpleName();
    PreferencesManager preferencesManager = PreferencesManager.getInstance();

    public DeleteTokenService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // Check for current token
            String originalToken = getTokenFromPrefs();
            Log.d(TAG, "Token before deletion: " + originalToken);
            // Resets Instance ID and revokes all tokens.
            FirebaseInstanceId.getInstance().deleteInstanceId();
            // Clear current saved token
            saveTokenToPrefs("");
            // Check for success of empty token
            String tokenCheck = getTokenFromPrefs();
            Log.d(TAG, "Token deleted. Proof: " + tokenCheck);
            // Now manually call onTokenRefresh()
            Log.d(TAG, "Getting new token");
            FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTokenToPrefs(String token) {

        preferencesManager.setFirebaseToken(token);
    }

    private String getTokenFromPrefs() {
        String deviceToken = preferencesManager.getFirebaseToken();
        return deviceToken;
    }
}