package com.ros.smartrocket.utils;

import android.text.TextUtils;
import android.widget.TextView;

import com.helpshift.support.Support;
import com.ros.smartrocket.App;
import com.ros.smartrocket.R;

import java.util.Locale;

public class LocaleUtils {
    public static final String DEFAULT_LANG = java.util.Locale.getDefault().toString();
    public static final String[] VISIBLE_LANGS_CODE = new String[]{"en", "zh_CN", "zh_HK", "zh_TW", "fr", "ar", "in", "ms"};
    public static String[] VISIBLE_LANGUAGE = new String[]{getStringById(R.string.english), getStringById(R.string.chinese_simple),
            getStringById(R.string.chinese_traditional_hk), getStringById(R.string.chinese_traditional_tw),
            getStringById(R.string.french), getStringById(R.string.arabic),
            getStringById(R.string.indonesian), getStringById(R.string.malaysian)};

    public static boolean setDefaultLanguage(String languageCode) {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        boolean languageChanged = !preferencesManager.getLanguageCode().equals(languageCode);
        preferencesManager.setLanguageCode(languageCode);
        Support.setSDKLanguage(getHsLanguage());
        return languageChanged;
    }

    private static String getHsLanguage() {
        return getCurrentLocale().getLanguage().equals("in") ? "id" : getCurrentLocale().getLanguage();
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
            locale = new Locale("ar", "MA");
            Support.setSDKLanguage("ar");
        } else if (isIndonesianLanguage(currentLanguageCode)) {
            locale = new Locale("in");
            Support.setSDKLanguage("id");
        } else if (isMalaysianLanguage(currentLanguageCode)) {
            locale = new Locale("ms");
            Support.setSDKLanguage("ms");
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

    private static boolean isIndonesianLanguage(String languageCode) {
        return languageCode.startsWith("in");
    }

    private static boolean isMalaysianLanguage(String languageCode) {
        return languageCode.startsWith("ms");
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
                case "in":
                case "in_ID":
                    languageCode = "in";
                    break;
                case "ms":
                case "ms_MY":
                case "ms_BN":
                case "ms_SG":
                    languageCode = "ms";
                    break;
                default:
                    languageCode = "en";
                    break;
            }
        }
        return languageCode;
    }

    private static String getLanguageCodeFromSupported() {
        if (isArabicLanguage(DEFAULT_LANG)) {
            return "ar";
        } else if (isFrenchLanguage(DEFAULT_LANG)) {
            return "fr";
        }else if (isIndonesianLanguage(DEFAULT_LANG)) {
            return "in";
        }else if (isMalaysianLanguage(DEFAULT_LANG)) {
            return "ms";
        } else if (isSimpleChinaLanguage(DEFAULT_LANG) || isTraditionalChinaHKLanguage(DEFAULT_LANG) || isTraditionalChinaHKLanguage(DEFAULT_LANG)) {
            return DEFAULT_LANG;
        } else {
            return "en";
        }
    }


    public static void setCurrentLanguage() {
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        String supportedLanguage = preferencesManager.getLanguageCode();
        if (!TextUtils.isEmpty(supportedLanguage)) {
            setDefaultLanguage(supportedLanguage);
        } else {
            supportedLanguage = getLanguageCodeFromSupported();
            preferencesManager.setLanguageCode(supportedLanguage);
            setDefaultLanguage(supportedLanguage);
        }
    }

    public static boolean isChinaLanguage() {
        String code = PreferencesManager.getInstance().getLanguageCode();
        return isSimpleChinaLanguage(code) || isTraditionalChinaHKLanguage(code) || isTraditionalChinaTWLanguage(code);
    }

    private static String getStringById(int resId) {
        return App.getInstance().getString(resId);
    }

    public static boolean isRtL() {
        return isArabicLanguage(PreferencesManager.getInstance().getLanguageCode());
    }

    public static void setCompoundDrawable(TextView view, int drawableRes) {
        if (isRtL()) {
            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0);
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(drawableRes, 0, 0, 0);
        }
    }
}
