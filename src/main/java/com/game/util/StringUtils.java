package com.game.util;

/**
 * Created by jeniss on 17/6/18.
 */
public class StringUtils {
    public static boolean isEmpty(String context) {
        if (context == null || context.trim().length() == 0) {
            return true;
        }
        return false;
    }
}
