package com.game.service;

import com.game.model.TradeFlow;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
public interface ITradeFlowService {
    void postTradeFlowBatch(List<TradeFlow> tradeFlowList);
}
