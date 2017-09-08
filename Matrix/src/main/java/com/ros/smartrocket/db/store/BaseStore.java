package com.ros.smartrocket.db.store;

import android.content.ContentResolver;

import com.ros.smartrocket.App;

class BaseStore {
    ContentResolver contentResolver;

    public BaseStore() {
        contentResolver = App.getInstance().getContentResolver();
    }
}
