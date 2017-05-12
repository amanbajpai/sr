package com.ros.smartrocket.utils;

import android.text.TextUtils;

import com.helpshift.support.Support;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;

import java.util.Locale;

public class LocaleUtils {
    public static final String DEFAULT_LANG = java.util.Locale.getDefault().toString();
    private static final String[] SUPPORTED_LANGS_CODE = new String[]{"en", "zh", "zh_CN", "zh_TW", "en_SG", "zh_HK", "fr", "fr_FR", "fr_CA", "fr_BE", "ar"};
    public static final String[] VISIBLE_LANGS_CODE = new String[]{"en", "zh_CN", "zh_HK", "zh_TW", "fr", "ar"};
    public static String[] VISIBLE_LANGUAGE = new String[]{getStringById(R.string.english), getStringById(R.string.chinese_simple),
            getStringById(R.string.chinese_traditional_hk), getStringById(R.string.chinese_traditional_tw),
            getStringById(R.string.french), getStringById(R.string.arabic)};

    public static boolean setDefaultLanguage(String languageCode) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        boolean languageChanged = !preferencesManager.getLanguageCode().equals(languageCode);
        preferencesManager.setLanguageCode(languageCode);
        Support.setSDKLanguage(getCurrentLocale().getLanguage());
        return languageChanged;
    }

    public static Locale getCurrentLocale() {
        String currentLanguageCode = PreferencesManager.getInstance().getLanguageCode();
        Locale locale;
        if (isSimpleChinaLanguage(currentLanguageCode)) {
            locale = Locale.SIMPLIFIED_CHINESE;
            Support.setSDKLanguage("zh_CN");
        } else if (isTraditionalChinaHKLanguage(currentLanguageCode)) {
            locale = new Locale("zh", "HK");
            Support.setSDKLanguage("zh_HK");
        } else if (isTraditionalChinaTWLanguage(currentLanguageCode)) {
            locale = Locale.TRADITIONAL_CHINESE;
            Support.setSDKLanguage("zh_TW");
        } else if (isFrenchLanguage(currentLanguageCode)) {
            locale = Locale.FRENCH;
            Support.setSDKLanguage("fr");
        } else if (isArabicLanguage(currentLanguageCode)) {
            locale = new Locale("ar");
            Support.setSDKLanguage("ar");
        } else {
            locale = new Locale(currentLanguageCode);
            Support.setSDKLanguage(currentLanguageCode);
        }
        return locale;
    }

    private static boolean isFrenchLanguage(String languageCode) {
        return "fr".equals(languageCode) || "fr_BE".equals(languageCode) || "fr_FR".equals(languageCode) || "fr_CA".equals(languageCode) || "fr_CH".equals(languageCode);
    }

    private static boolean isSimpleChinaLanguage(String languageCode) {
        return "zh".equals(languageCode) || "zh_CN".equals(languageCode) || "en_SG".equals(languageCode);
    }

    private static boolean isTraditionalChinaHKLanguage(String languageCode) {
        return "zh_HK".equals(languageCode);
    }

    private static boolean isTraditionalChinaTWLanguage(String languageCode) {
        return "zh_TW".equals(languageCode);
    }

    private static boolean isArabicLanguage(String languageCode) {
        return languageCode.startsWith("ar");
    }

    public static String getCorrectLanguageCode(String languageCode) {
        if (!TextUtils.isEmpty(languageCode)) {
            switch (languageCode) {
                case "zh":
                case "en_SG":
                case "zh_CN":
                    languageCode = "zh_CN";
                    break;
                case "zh_TW":
                    languageCode = "zh_TW";
                    break;
                case "zh_HK":
                    languageCode = "zh_HK";
                    break;
                case "fr":
                case "fr_FR":
                case "fr_CA":
                case "fr_BE":
                    languageCode = "fr";
                    break;
                case "ar":
                    languageCode = "ar";
                    break;
                default:
                    languageCode = "en";
                    break;
            }
        }
        return languageCode;
    }

    private static String getLanguageCodeFromSupported() {
        for (String lc : SUPPORTED_LANGS_CODE) {
            if (DEFAULT_LANG.equals(lc)) {
                return DEFAULT_LANG;
            }
        }
        return "en";
    }


    public static void setCurrentLanguage() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();

        if (!TextUtils.isEmpty(preferencesManager.getLanguageCode())) {
            setDefaultLanguage(preferencesManager.getLanguageCode());
        } else {
            String supportedLanguage = getLanguageCodeFromSupported();
            preferencesManager.setLanguageCode(supportedLanguage);
            setDefaultLanguage(supportedLanguage);
        }
    }

    private static String getStringById(int resId) {
        return App.getInstance().getString(resId);
    }

}
