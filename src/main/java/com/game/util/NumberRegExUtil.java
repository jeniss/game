package com.game.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jeniss on 17/7/6.
 */
public class NumberRegExUtil {
    private static final Map<String, Integer> numMap = new HashMap<String, Integer>();

    static {
        numMap.put("一", 1);
        numMap.put("二", 2);
        numMap.put("三", 3);
        numMap.put("四", 4);
        numMap.put("五", 5);
        numMap.put("六", 6);
        numMap.put("七", 7);
        numMap.put("八", 8);
        numMap.put("九", 9);
        numMap.put("十", 10);
    }

    /**
     * get roman number in content
     * @param content
     */
    public static List<Double> getNumberByRomanNum(String content, String unit, String withoutUnit) {
        String numberPattern = "\\d{1,5}+(\\.\\d+)?";
        if (unit != null) {
            numberPattern += unit;
        } else if (withoutUnit != null) {
            numberPattern += withoutUnit;
        }

        List<Double> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(numberPattern);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String matcherNumber = matcher.group();
            if (unit != null) {
                matcherNumber = matcherNumber.substring(0, matcherNumber.length() - 1);
            }

            Double count = Double.valueOf(matcherNumber);
            if (!result.contains(count)) {
                result.add(count);
            }
        }

        return result;
    }

    /**
     * get roman number(1~99) from zh in content
     * @param content
     */
    public static List<Double> getNumberByZhNum(String content, String unit) {
        List<Double> result = new ArrayList<>();
        //(一|二|三|四|五|六|七|八|九|十){1,3}
        String regexZhNumber = "(\\u4e00|\\u4e8c|\\u4e09|\\u56db|\\u4e94|\\u516d|\\u4e03|\\u516b|\\u4e5d|\\u5341){1,3}";
        if (unit != null) {
            //(一|二|三|四|五|六|七|八|九|十){1,3}【unit】
            regexZhNumber += unit;
        }

        Pattern pattern = Pattern.compile(regexZhNumber);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String matcherNumber = matcher.group();
            if (unit != null) {
                matcherNumber = matcherNumber.substring(0, matcherNumber.length() - 1);
            }

            List<Integer> charList = new ArrayList<>();
            for (int i = 0; i < matcherNumber.length(); i++) {
                charList.add(numMap.get(String.valueOf(matcherNumber.charAt(i))));
            }

            Double number = Double.valueOf(charList.get(0));
            if (charList.size() == 2) {
                number = Double.valueOf(charList.get(0) * charList.get(1));
            } else if (charList.size() == 3) {
                number = Double.valueOf(charList.get(0) * charList.get(1) + charList.get(2));
            }

            if (!result.contains(number)) {
                result.add(number);
            }
        }
        return result;
    }

    /**
     * check content without roman number
     * @param content
     */
    public static boolean checkContentWithoutRomanNumber(String content) {
        String numberPattern = "\\d{1,5}+(\\.\\d+)?";

        Pattern pattern = Pattern.compile(numberPattern);
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            return true;
        }
        return false;
    }

    //转换为unicode
    private static String encodeUnicode(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i : utfBytes) {
            String hexB = Integer.toHexString(i);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
}
