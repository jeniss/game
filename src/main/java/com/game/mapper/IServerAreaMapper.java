package com.game.mapper;

import com.game.model.ServerArea;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by jennifert on 5/18/2017.
 */
public interface IServerAreaMapper {
    List<ServerArea> getAll();

    ServerArea getServerAreaByParentIdAndCode(@Param("parentId") Integer parentId, @Param("code") String code);
}
