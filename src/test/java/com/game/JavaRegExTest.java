package com.game;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jennifert on 7/6/2017.
 */
public class JavaRegExTest {
    public static void main(String[] args) {
        //        String s = "我有十块钱, 你有二十三块钱, 他有二元钱";
        //        Pattern p;
        //        Matcher m;
        //        for (String regex : regexMap.keySet()) {
        //            p = Pattern.compile(regex);
        //            m = p.matcher(s);
        //            while (m.find()) {
        //                String exper = regexMap.get(regex);
        //                List<String> list = new ArrayList<String>();
        //                for (int i = 1; i <= m.groupCount(); i++) {
        //                    list.add(NumRegex.numMap.get(m.group(i)));
        //                }
        //                exper = MessageFormat.format(exper, list.toArray());
        //                String text = m.group();
        //                String value = experToValue(exper);
        //                s = s.replace(text, value);
        //            }
        //        }
        //        System.out.println(s);

//        StringBuilder regex = new StringBuilder();
//        //                String[] zhNumbers = new String[]{"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百"};
//        String[] zhNumbers = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
//        int j = 0;
//        for (String zhNumber : zhNumbers) {
//            regex.append(encodeUnicode(zhNumber) + "|");
//        }
//        System.out.println(regex.toString());

//        String regexNumber = "\\d{1,3}个";
        String regexNumber = "\\d{1,3}+(\\.\\d+)?个";
//        String regexZhNumber = "(\\u96f6|\\u4e00|\\u4e8c|\\u4e09|\\u56db|\\u4e94|\\u516d|\\u4e03|\\u516b|\\u4e5d|\\u5341){1,3}个";


//        String test = "我有十个苹果, 你有二十三个桃子, 他有二个橘子, 三百零一个,四百三个";
        String test = "我有10个苹果, 你有233个桃子, 他有2个橘子,2.334个, 0.56个";
        Pattern pattern = Pattern.compile(regexNumber);
        Matcher matcher = pattern.matcher(test);
        while (matcher.find()) {
            String text = matcher.group();
            text = text.replace("个", "");
            System.out.println(text);

//            List<Integer> countList = new ArrayList<>();
//            for (char charStr : text.toCharArray()) {
//                int count = NumRegex.numMap.get(String.valueOf(charStr));
//                countList.add(count);
//                System.out.println(count);
//            }
//
//            int totalCount = countList.get(0);
//            if (countList.size() > 1) {
//                totalCount = 0;
//                for (int i = 0; i < countList.size(); i += 2) {
//                    if (i < countList.size() - 1) {
//                        totalCount += countList.get(i) * countList.get(i + 1);
//                    } else {
//                        totalCount += countList.get(i);
//                    }
//                }
//            }
//
//            System.out.println("totalCount:" + totalCount);
        }
    }

    public static String experToValue(String exper) {
        String[] experArr = null;
        experArr = exper.split(encodeUnicode("+"));

        int value = 0;
        for (String sExper : experArr) {
            String[] sExperArr = sExper.split(encodeUnicode("*"));
            value += Integer.valueOf(sExperArr[0]) * Integer.valueOf(sExperArr[1]);
        }
        return String.valueOf(value);
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


    //一、十一、二十一、三百二十一、三百零一、二十、三百、三百二、十
    private static final Map<String, String> regexMap = new LinkedHashMap<String, String>();

    static {
        //三百二十一
        String regex = NumRegex.getNumRegex() + encodeUnicode("百") + NumRegex.getNumRegex() + encodeUnicode("十") + NumRegex.getNumRegex();
        String exper = "{0}*100+{1}*10+{2}*1";
        regexMap.put(regex, exper);
        //三百零一
        regex = NumRegex.getNumRegex() + encodeUnicode("百") + encodeUnicode("零") + NumRegex.getNumRegex();
        exper = "{0}*100+{1}*1";
        regexMap.put(regex, exper);
        //三百二
        regex = NumRegex.getNumRegex() + encodeUnicode("百") + NumRegex.getNumRegex();
        exper = "{0}*100+{1}*10";
        regexMap.put(regex, exper);
        //三百
        regex = NumRegex.getNumRegex() + encodeUnicode("百");
        exper = "{0}*100";
        regexMap.put(regex, exper);
        //二十一
        regex = NumRegex.getNumRegex() + encodeUnicode("十") + NumRegex.getNumRegex();
        exper = "{0}*10+{1}*1";
        regexMap.put(regex, exper);
        //二十
        regex = NumRegex.getNumRegex() + encodeUnicode("十");
        exper = "{0}*10";
        regexMap.put(regex, exper);
        //十一
        regex = encodeUnicode("十") + NumRegex.getNumRegex();
        exper = "1*10+{0}*1";
        regexMap.put(regex, exper);
        //十
        regex = encodeUnicode("十");
        exper = "1*10";
        regexMap.put(regex, exper);
        //一
        regex = NumRegex.getNumRegex();
        exper = "{0}*1";
        regexMap.put(regex, exper);
    }

    static class NumRegex {
        public static final Map<String, Integer> numMap = new HashMap<String, Integer>();

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
            numMap.put("百", 100);
        }

        private static String numRegex;

        public static String getNumRegex() {
            if (numRegex == null || numRegex.length() == 0) {
                numRegex = "([";
                for (String s : numMap.keySet()) {
                    numRegex += encodeUnicode(s);
                }
                numRegex += "])";
            }
            return numRegex;
        }
    }


}
