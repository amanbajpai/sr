package com.ros.smartrocket.utils;

import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;

public class AssetFontsCache {
    private final static String TAG = "AssetFontsCache";

    private AssetFontsCache() {}

    protected final static Map<String, Typeface> loadedTypefacesMap = new HashMap<String, Typeface>();

    public static Typeface loadFontFromAsset(AssetManager assetManager, String assetPath) {
        Typeface t = loadedTypefacesMap.get(assetPath);
        if (t == null) {
            try {
                t = Typeface.createFromAsset(assetManager, assetPath);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load typeface from font asset '" + assetPath + "'");
                e.printStackTrace();
                return null;
            }

            loadedTypefacesMap.put(assetPath, t);
        }
        return t;
    }
}
