package com.ros.smartrocket.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;


public class FontUtils {
    private final static String TAG = "FontUtils";
    private final static Map<String, Typeface> LOADED_TYPEFACES_MAP = new HashMap<String, Typeface>();
    private final static PreferencesManager PREFERENCES_MANAGER = PreferencesManager.getInstance();

    public static String getFontAssetPath(int textStyle) {
        String fontAssetPath;
        String languageCode = PREFERENCES_MANAGER.getLanguageCode();

        switch (textStyle) {
            case 0:
                if ("zh_TW".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKMedium.otf";
                } else if ("zh_CN".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKSMedium.otf";
                } else {
                    fontAssetPath = "fonts/LatinoMagallanesRegular.otf";
                }
                break;
            case 1:
                if ("zh_TW".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKLight.otf";
                } else if ("zh_CN".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKLight.otf";
                } else {
                    fontAssetPath = "fonts/LatinoMagallanesLight.otf";
                }
                break;
            case 2:
                if ("zh_TW".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKMedium.otf";
                } else if ("zh_CN".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKSMedium.otf";
                } else {
                    fontAssetPath = "fonts/LatinoMagallanesMedium.otf";
                }
                break;
            case 3:
                if ("zh_TW".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKBold.otf";
                } else if ("zh_CN".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKSBold.otf";
                } else {
                    fontAssetPath = "fonts/LatinoMagallanesBold.otf";
                }
                break;
            default:
                if ("zh_TW".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKMedium.otf";
                } else if ("zh_CN".equals(languageCode)) {
                    fontAssetPath = "fonts/MHeiHKSMedium.otf";
                } else {
                    fontAssetPath = "fonts/LatinoMagallanesMedium.otf";
                }
                break;
        }

        return fontAssetPath;
    }

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
