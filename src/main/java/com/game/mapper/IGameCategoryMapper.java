package com.game.mapper;

import com.game.model.GameCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
public interface IGameCategoryMapper {
    List<GameCategory> getAllItemCategories();

    List<GameCategory> getAllKeysByItemCode(@Param("itemCode") String itemCode);
}
