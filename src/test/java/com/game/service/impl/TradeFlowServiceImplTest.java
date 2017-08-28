package com.game.service.impl;

import com.game.BaseTest;
import com.game.mapper.ITradeFlowMapper;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.model.TradeFlow;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jennifert on 08/28/17.
 */
public class TradeFlowServiceImplTest extends BaseTest {
    @Autowired
    private ITradeFlowMapper tradeFlowMapper;

    @Test
    public void postTradeFlowBatch() throws Exception {
        List<TradeFlow> tradeFlowList = new ArrayList<>();

        TradeFlow tradeFlow = new TradeFlow();
        tradeFlow.setGame(new Game(1));
        tradeFlow.setServerArea(new ServerArea(2));
        tradeFlow.setGameCategory(new GameCategory(1));
        tradeFlow.setName("45元=648100金 ★手工打造，安全可靠★");
        tradeFlow.setPrice(45.0);
        tradeFlow.setStock(6);
        tradeFlow.setTotalPrice(270.0);
        tradeFlow.setUnitPriceDesc("1元=14402.2222金");
        tradeFlow.setTradeStatus("selling");
        tradeFlow.setEntryDatetime(new Date());
        tradeFlowList.add(tradeFlow);
        tradeFlowMapper.postTradeFlowBatch(tradeFlowList);
    }

}