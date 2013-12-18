package com.ros.smartrocket.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BytesBitmap {

    public static Bitmap getBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream baops = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 0, baops);
        return baops.toByteArray();
    }

    public static String getBase64String(Bitmap bitmap){
        byte[] ba = getBytes(bitmap);
        return Base64.encodeToString(ba, Base64.DEFAULT);
    }
}