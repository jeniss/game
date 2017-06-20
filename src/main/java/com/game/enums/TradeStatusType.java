package com.game.enums;

/**
 * Created by jeniss on 17/6/18.
 */
public enum TradeStatusType {
    finished("交易完成"),
    trading("正在交易"),
    selling("立即购买");

    private String desc;

    TradeStatusType(String desc) {
        this.desc = desc;
    }

    public static TradeStatusType getTradeStatusTypeByDesc(String desc) {
        for (TradeStatusType tradeStatusType : TradeStatusType.values()) {
            if (tradeStatusType.desc.equals(desc)) {
                return tradeStatusType;
            }
        }
        return null;
    }
}
