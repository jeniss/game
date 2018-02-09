package com.game.service;

import com.game.model.ServerArea;

import java.util.List;

/**
 * Created by jennifert on 5/18/2017.
 */
public interface IServerAreaService {
    List<ServerArea> getAllByGameId(Integer gameId);

    ServerArea getServerAreaByParentIdAndCode(Integer parentId, String code);
}
