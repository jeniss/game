package com.game.service.impl;

import com.game.mapper.IConfigMapper;
import com.game.model.Config;
import com.game.service.IConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by jeniss on 17/5/23.
 */
@Service
public class ConfigServiceImpl implements IConfigService {
    @Resource
    private IConfigMapper configMapper;

    @Override
    public Config getConfigByCode(String code) {
        return configMapper.getConfigByCode(code);
    }
}
