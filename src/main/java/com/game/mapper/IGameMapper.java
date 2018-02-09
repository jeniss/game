package com.game.mapper;

import com.game.model.Game;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by jeniss on 17/5/23.
 */
public interface IGameMapper {
    List<Game> getAllGameList();

    List<Game> getActiveGameList();

    Game getGameByCode(@Param("code") String code);
}
