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
}