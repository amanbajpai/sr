package com.ros.smartrocket.utils;

public class ValidationUtils {

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
