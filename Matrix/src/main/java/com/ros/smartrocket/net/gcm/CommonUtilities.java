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
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ros.smartrocket.App;
import com.ros.smartrocket.Config;
import com.ros.smartrocket.net.helper.GcmRegistrar;
import com.ros.smartrocket.utils.L;
import com.ros.smartrocket.utils.PreferencesManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class CommonUtilities {
    /**
     * Intent used to display a message in the screen.
     */
    public static final String DISPLAY_MESSAGE_ACTION = "com.ros.smartrocket.net.gcm.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    public static String imagePath = null;

    private static final String TAG = CommonUtilities.class.getSimpleName();

    private CommonUtilities() {
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
                    String registrationId = gcm.register(Config.GCM_SENDER_ID);
                    L.i(TAG, "Device registered, registration ID=" + registrationId);

                    if (!Config.USE_BAIDU) {
                        L.d(TAG, "Send registered to server: regId = " + registrationId);
                        new GcmRegistrar().registerGCMId(registrationId, 0);
                        //TODO uncomment after new GCM implementation
                        //Core.registerDeviceToken(App.getInstance(), registrationId);
                        PreferencesManager.getInstance().setGCMRegistrationId(registrationId);
                    }

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //APIFacade.getInstance().registerGCMId(App.getInstance(), regId);

                    // Persist the regID - no need to register again.
                    //PreferencesManager.getInstance().setGCMRegistrationId(regId);

                    return registrationId;
                } catch (IOException e) {
                    L.e(TAG, "registerGCMInBackground() [Error :" + e.getMessage() + "]", e);
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

    public static void exportDB(Context context) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "com.ros.smartrocket" + "/databases/" + "matrix_db";
        String backupDBPath = "matrix_db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(context, "DB Exported!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
