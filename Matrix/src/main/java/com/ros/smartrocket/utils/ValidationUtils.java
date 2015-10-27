package com.ros.smartrocket.utils;

/**
 * Created by macbook on 27.10.15.
 */
public class ValidationUtils {

    public static boolean containsLettersOnly(String s) {
        String regex = "[a-zA-Z\\u4e00-\\u9eff]+";
        return s.matches(regex) || s.matches(regex);
    }

    public static boolean containsNumbersOnly(String s) {
        String regex = "[0-9]+";
        return s.matches(regex);
    }

    public static boolean validEmail(String s) {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" +
                "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return s.matches(regex);
    }

    public static boolean validChinaPhone(String s) {
        return containsNumbersOnly(s) && s.length() == 11 && s.charAt(0) == '1';
    }
}
