/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ros.smartrocket.net.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Keys;
import com.ros.smartrocket.helpers.APIFacade;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;

import java.io.IOException;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {

    /**
     * Google API project id registered to use GCM.
     */
    public static final String SENDER_ID = Keys.GCM_ID;

    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION = "com.ros.smartrocket.gcm.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    private static final String TAG = CommonUtilities.class.getSimpleName();

    public CommonUtilities() {

    }

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

    /**
     * We should call this methods right after the success login
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    public static void registerGCMInBackground() {
        new AsyncTask<Void, String, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(App.getInstance());
                    String regId = gcm.register(CommonUtilities.SENDER_ID);
                    L.i(TAG, "Device registered, registration ID=" + regId);

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    APIFacade.getInstance().registerGCMId(App.getInstance(), regId);

                    // Persist the regID - no need to register again.
                    PreferencesManager.getInstance().setGCMRegistrationId(regId);

                    return regId;
                } catch (IOException ex) {
                    L.e(TAG, "registerGCMInBackground() [Error :" + ex.getMessage() + "]");
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                L.i(TAG, "onPostExecute");
            }
        }.execute();
    }
}
