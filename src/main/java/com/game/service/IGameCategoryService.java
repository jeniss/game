package com.game.service;

import com.game.model.GameCategory;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
public interface IGameCategoryService {
    List<GameCategory> getAllItemCategoriesByGameId(Integer gameId);

    List<GameCategory> getAllKeysByItemCode(String itemCode);

    GameCategory getItemCategoryByValue(String value);

    GameCategory getGameCategoryByParentIdAndName(Integer parentId, String name);
}
