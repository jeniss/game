package com.game.service.impl;

import com.game.mapper.IConfigMapper;
import com.game.model.Config;
import com.game.service.IConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jeniss on 17/5/23.
 */
@Service(value = "configService")
public class ConfigServiceImpl implements IConfigService {
    @Resource
    private IConfigMapper configMapper;

    @Override
    public List<Config> getAllConfig() {
        return configMapper.getAllConfig();
    }
}
