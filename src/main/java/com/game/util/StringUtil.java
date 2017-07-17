package com.game.util;

/**
 * Created by jeniss on 17/6/18.
 */
public class StringUtil {
    public static boolean isEmpty(String context) {
        if (context == null || context.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        if (!isEmpty(str)) {
            for (char c : str.toCharArray()) {
                if (!Character.isDigit(c)) {
                    return false;
                }
            }
        }
        return false;
    }
}
