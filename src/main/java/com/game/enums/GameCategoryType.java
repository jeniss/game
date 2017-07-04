package com.game.enums;

/**
 * Created by jeniss on 17/7/4.
 */
public enum GameCategoryType {
    gameCoin("游戏币"),
    equipment("装备");

    private String typeName;

    GameCategoryType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
