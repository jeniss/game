package com.game.service.impl;

import com.game.exception.BizException;
import com.game.mapper.ITradeFlowMapper;
import com.game.model.TradeFlow;
import com.game.service.ITradeFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
@Service
public class TradeFlowServiceImpl implements ITradeFlowService {
    @Autowired
    ITradeFlowMapper tradeFlowMapper;

    @Override
    @Transactional
    public void postTradeFlowBatch(List<TradeFlow> tradeFlowList) {
        if (CollectionUtils.isEmpty(tradeFlowList)){
            throw new BizException("the trade flow list is null");
        }
        tradeFlowMapper.postTradeFlowBatch(tradeFlowList);
    }
}
