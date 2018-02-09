package com.game.service;

import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import org.openqa.selenium.WebDriver;

/**
 * Created by jeniss on 17/12/10.
 */
public interface ISeleniumProcessHTMLService {
    Boolean processHtmlAndPost(WebDriver webDriver, Game game, ServerArea serverArea, ServerArea childServer, GameCategory gameCategory, GameCategory keyCategory);
}
