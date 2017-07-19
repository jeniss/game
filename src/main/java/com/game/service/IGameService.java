package com.game.service;

import com.game.model.Game;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
public interface IGameService {
    List<Game> getAllGameList();

    Game getGameByCode(String code);
}
