package com.ros.smartrocket.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;


public class FontUtils {
    private final static String TAG = "FontUtils";
    private final static Map<String, Typeface> LOADED_TYPEFACES_MAP = new HashMap<String, Typeface>();
    //private final static PreferencesManager PREFERENCES_MANAGER = PreferencesManager.getInstance();


    /**
     * Return font path by style
     */

    public static String getFontAssetPath(int textStyle) {
        String fontAssetPath;

        switch (textStyle) {
            case 0:
                fontAssetPath = "fonts/LatinoMagallanesRegular.otf";
                break;
            case 1:
                fontAssetPath = "fonts/LatinoMagallanesLight.otf";
                break;
            case 2:
                fontAssetPath = "fonts/LatinoMagallanesMedium.otf";
                break;
            case 3:
                fontAssetPath = "fonts/LatinoMagallanesBold.otf";
                break;
            default:
                fontAssetPath = "fonts/LatinoMagallanesMedium.otf";
                break;
        }

        return fontAssetPath;
    }

    /**
     * Get typeface from asset
     */
    public static Typeface loadFontFromAsset(AssetManager assetManager, String assetPath) {
        Typeface t = LOADED_TYPEFACES_MAP.get(assetPath);
        if (t == null) {
            try {
                t = Typeface.createFromAsset(assetManager, assetPath);
            } catch (Exception e) {
                L.e(TAG, "Failed to load typeface from font asset '" + assetPath + "'", e);
                return null;
            }

            LOADED_TYPEFACES_MAP.put(assetPath, t);
        }
        return t;
    }
}
