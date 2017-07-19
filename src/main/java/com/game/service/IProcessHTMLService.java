package com.game.service;

import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.model.TradeFlow;

import java.util.List;

/**
 * Created by jennifert on 7/19/2017.
 */
public interface IProcessHTMLService {
    void processHtmlAndPost(Game game, ServerArea serverArea, ServerArea childServer, GameCategory gameCategory, String urlStr);

    List<TradeFlow> getGameCoinTradeFlow(String urlStr, Game game, GameCategory gameCategory, ServerArea childServer) throws Exception;

    List<TradeFlow> getEquipmentTradeFlow(List<TradeFlow> tradeFlowList, String urlStr, Game game, GameCategory keyCategory, ServerArea childServer) throws Exception;
}
