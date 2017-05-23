package com.game.mapper;

import com.game.model.Config;
import org.apache.ibatis.annotations.Param;

/**
 * Created by jeniss on 17/5/23.
 */
public interface IConfigMapper {
    Config getConfigByCode(@Param("code") String code);
}
