package com.game.service;

import com.game.model.Config;

/**
 * Created by jeniss on 17/5/23.
 */
public interface IConfigService {
    Config getConfigByCode(String code);
}
