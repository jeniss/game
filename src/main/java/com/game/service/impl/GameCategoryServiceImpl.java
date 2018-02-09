package com.game.service.impl;

import com.game.mapper.IGameCategoryMapper;
import com.game.model.GameCategory;
import com.game.service.IGameCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
@Service(value = "gameCategoryService")
public class GameCategoryServiceImpl implements IGameCategoryService {
    @Autowired
    IGameCategoryMapper gameCategoryMapper;

    @Override
    public List<GameCategory> getAllItemCategoriesByGameId(Integer gameId) {
        return gameCategoryMapper.getAllItemCategoriesByGameId(gameId);
    }

    @Override
    public List<GameCategory> getAllKeysByItemCode(String itemCode) {
        return gameCategoryMapper.getAllKeysByItemCode(itemCode);
    }

    @Override
    public GameCategory getItemCategoryByValue(String value) {
        return gameCategoryMapper.getItemCategoryByValue(value);
    }

    @Override
    public GameCategory getGameCategoryByParentIdAndName(Integer parentId, String name) {
        return gameCategoryMapper.getGameCategoryByParentIdAndName(parentId, name);
    }
}
