package com.game.service;

import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;

/**
 * Created by jennifert on 7/19/2017.
 */
public interface IProcessHTMLService {
    void processHtmlAndPost(Game game, ServerArea serverArea, ServerArea childServer, GameCategory gameCategory, String urlStr);
}
