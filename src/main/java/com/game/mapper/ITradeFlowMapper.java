package com.game.mapper;

import com.game.model.TradeFlow;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
public interface ITradeFlowMapper {
    void postTradeFlowBatch(List<TradeFlow> tradeFlowList);
}
