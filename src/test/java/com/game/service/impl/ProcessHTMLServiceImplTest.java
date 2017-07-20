package com.game.service.impl;

import com.game.BaseTest;
import com.game.service.IProcessHTMLService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by jennifert on 7/19/2017.
 */
public class ProcessHTMLServiceImplTest extends BaseTest {
    @Autowired
    private IProcessHTMLService processHTMLService;

    @Test
    public void getGameCoinTradeFlowTest() throws Exception {
//        String urlStr = "D:\\test.html";
//        List<TradeFlow> tradeFlowList = processHTMLService.getGameCoinTradeFlow(urlStr, null, null, null);
//        System.out.println("size:" + tradeFlowList.size());
    }

    @Test
    public void getEquipmentTradeFlowTest() throws Exception {

    }

    @Test
    public void processCountTest() throws Exception {
        String[] contents = {"【〖1000个聚宝盆〗★安全赔付】【1000个聚宝盆〗★安全赔付★★安全赔付，找回立刻赔★",
                "【1000聚宝盆】1000聚宝盆★安全赔付，找回立刻赔★",
                "【碧涛仙玉】3件绿玉（碧涛仙玉）超值小单低价处理★安全赔付，找回立刻赔★",
                "【沐雨令一枚】召唤雨神必备，有几率爆无极印、十铁、圣级符等如图，全区唯一。★安全赔付，找回立刻赔★",
                "【碧涛仙玉3枚】有实力的老板拿去换自己喜欢的宝物如图，信誉第一，安全保障。★安全赔付，找回立刻赔★",
                "53张飞天神符 ★安全赔付，找回立刻赔★", "【聚宝盆】1000举报★安全赔付，找回立刻赔★",
                "【〖碧涛仙玉〗】【碧涛仙玉】★安全赔付，找回立刻赔★",
                "【十铁碎片礼包】1个拿去合★安全赔付，找回立刻赔★",
                "【金精铁玉10级】数量：1恭喜，用（10铁）十铁）炼器成功~专业品质信誉第一★安全赔付，找回立刻赔★",
                "【3阶伤心花一个+附件一套便宜丢】3阶5技能懂得秒比天琊斩龙剑十铁聚宝盆实惠★安全赔付，找回立刻赔★"};
        int i = 0;
        for (String content : contents) {
            i++;
//            System.out.println(i + ":" + processHTMLService.processCount(content));
        }
    }
}