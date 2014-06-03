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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Helper class used to communicate with the server to register device ID from GCM on our server
 */
public final class ServerUtilities {

    @SuppressWarnings("hiding")

    private Context context;
    private ResponseReceiver receiver;

    private IntentFilter filterRegister;
    private IntentFilter filterUnregister;
    private boolean loadingCompleted = true;

    public ServerUtilities(Context context) {
        this.context = context;

        receiver = new ResponseReceiver();
        filterRegister = new IntentFilter(ResponseReceiver.ACTION_REGISTER);
        filterRegister.addCategory(Intent.CATEGORY_DEFAULT);

        filterUnregister = new IntentFilter(ResponseReceiver.ACTION_UNREGISTER);
        filterUnregister.addCategory(Intent.CATEGORY_DEFAULT);

        context.registerReceiver(receiver, filterRegister);
        context.registerReceiver(receiver, filterUnregister);
    }

    private class ResponseReceiver extends BroadcastReceiver {

        public static final String ACTION_REGISTER = "com.ros.smartrocket.intent.action.REGISTER_DEVICE";
        public static final String ACTION_UNREGISTER = "com.ros.smartrocket.intent.action.UNREGISTER_DEVICE";

        @Override
        public void onReceive(Context context, Intent intent) {
//            Boolean success = intent.getBooleanExtra(NetworkConfig.PARAM_OUT_SUCCESS, false);
//
//            if (success) {
//                if (intent.getAction().equals(ACTION_REGISTER)) {
//                    GCMRegistrar.setRegisteredOnServer(context, true);
//                } else {
//                    L.d(TAG, "device unregistered from push notifications");
//                    GCMRegistrar.setRegisteredOnServer(context, false);
//                }
//            } else {
//                String message = intent.getStringExtra(NetworkConfig.PARAM_OUT_ERROR_MESSAGE);
//                String title = context.getResources().getString(R.string.dialog_error_title);
//                DialogHelper.showOkDialog(context, title, message);
//                L.d(TAG, "push notifications error: " + message);
//            }
            loadingCompleted = true;
        }
    }
}
