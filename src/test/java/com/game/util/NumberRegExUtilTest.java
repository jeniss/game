package com.game.util;

import org.junit.Test;

import java.util.List;

/**
 * Created by jennifert on 7/20/2017.
 */
public class NumberRegExUtilTest {
    @Test
    public void getNumberByRomanNumTest() throws Exception {
        String content = "【3阶伤心花一个+附件一套便宜丢】3阶5技能懂得秒比天琊斩龙剑十铁聚宝盆实惠★安全赔付，找回立刻赔★56";
//        String content = "【1个碧涛仙玉】（绿玉）低价卖！————————————————★安全赔付，找回立刻赔★";
//        List<Double> result = NumberRegExUtil.getNumberByRomanNum(content, "+(个|件|枚|张)", null);
        List<Double> result = NumberRegExUtil.getNumberByRomanNum(content, null, "+(?!级|阶|技)");
//        List<Double> result = NumberRegExUtil.getNumberByRomanNum(content, null);
        System.out.println(result.size());
    }

    @Test
    public void getNumberByZhNumTest() throws Exception {
        String content = "【十铁碎片礼包】十铁碎片礼包一个★安全赔付，找回立刻赔★";
        List<Double> result = NumberRegExUtil.getNumberByZhNum(content, "个");
        System.out.println(result.size());
    }

    @Test
    public void checkContentWithoutRomanNumberTest() throws Exception {
        String content = "【十铁碎片礼包】十铁碎片礼包一个★安全赔付10，找回立刻赔★";
        System.out.println(NumberRegExUtil.checkContentWithoutRomanNumber(content));
    }

}