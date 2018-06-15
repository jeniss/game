package com.game.service;

import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.plugins.phantomjs.GhostWebDriver;

/**
 * Created by jeniss on 17/12/10.
 */
public interface ISeleniumProcessHTMLService {
    String processHtmlAndPost(GhostWebDriver ghostWebDriver, Game game, ServerArea serverArea, ServerArea childServer, GameCategory gameCategory, GameCategory keyCategory);
}
