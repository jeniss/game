package com.game.service.impl;

import com.game.mapper.IGameMapper;
import com.game.model.Game;
import com.game.service.IGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by jeniss on 17/6/18.
 */
@Service(value = "gameService")
public class GameServiceImpl implements IGameService {
    @Autowired
    IGameMapper gameMapper;

    /**
     * get all game list
     */
    @Override
    public List<Game> getAllGameList() {
        return gameMapper.getAllGameList();
    }

    /**
     * gat all active game list
     * @return
     */
    @Override
    public List<Game> getActiveGameList() {
        return gameMapper.getActiveGameList();
    }

    /**
     * get game by code
     * @param code
     */
    @Override
    public Game getGameByCode(String code) {
        return gameMapper.getGameByCode(code);
    }
}
