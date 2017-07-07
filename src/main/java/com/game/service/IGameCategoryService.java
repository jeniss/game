package com.game.service;

import com.game.model.GameCategory;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
public interface IGameCategoryService {
    List<GameCategory> getAllItemCategories();

    List<GameCategory> getAllKeysByItemCode(String itemCode);
}
