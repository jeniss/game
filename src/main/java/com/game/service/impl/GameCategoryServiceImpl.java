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
    public List<GameCategory> getAllGameCategory() {
        return gameCategoryMapper.getAllGameCategory();
    }
}
