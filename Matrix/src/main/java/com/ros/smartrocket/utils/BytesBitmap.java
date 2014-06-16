package com.ros.smartrocket.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BytesBitmap {

    public BytesBitmap() {

    }

    /**
     * Convert byte array to bitmap
     */
    public static Bitmap getBitmap(byte[] data) {
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    /**
     * Convert bitmap to byte array
     */
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream baops = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baops);
        return baops.toByteArray();
    }

    /**
     * Convert bitmap to base64 string
     */
    public static String getBase64String(Bitmap bitmap) {
        byte[] ba = getBytes(bitmap);
        return Base64.encodeToString(ba, Base64.DEFAULT);
    }
}
