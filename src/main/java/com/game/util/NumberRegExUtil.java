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
    public static List<Integer> getNumberByRomanNum(String content) {
        String numberPattern = "\\d{1,3}个";

        List<Integer> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(numberPattern);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String matcherNumber = matcher.group();
            result.add(Integer.valueOf(matcherNumber.replace("个", "")));
        }
        return result;
    }

    /**
     * get roman number(1~99) from zh in content
     * @param content
     */
    public static List<Integer> getNumberByZhNum(String content) {
        List<Integer> result = new ArrayList<>();
        //(一|二|三|四|五|六|七|八|九|十){1,3}个
        String regexZhNumber = "(\\u4e00|\\u4e8c|\\u4e09|\\u56db|\\u4e94|\\u516d|\\u4e03|\\u516b|\\u4e5d|\\u5341){1,3}个";
        Pattern pattern = Pattern.compile(regexZhNumber);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String matcherNumber = matcher.group();
            matcherNumber = matcherNumber.replace("个", "");

            int[] charArray = new int[3];
            for (int i = 0; i < matcherNumber.length(); i++) {
                charArray[i] = numMap.get(String.valueOf(matcherNumber.charAt(i)));
            }

            Integer number = charArray[0];
            if (charArray.length == 2) {
                number = charArray[0] * charArray[1];
            } else if (charArray.length == 3) {
                number = charArray[0] * charArray[1] + charArray[2];
            }
            result.add(number);
        }
        return result;
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
