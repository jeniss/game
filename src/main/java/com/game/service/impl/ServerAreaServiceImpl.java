package com.game.service.impl;

import com.game.mapper.IServerAreaMapper;
import com.game.model.ServerArea;
import com.game.service.IServerAreaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jennifert on 5/18/2017.
 */
@Service(value = "serverAreaService")
public class ServerAreaServiceImpl implements IServerAreaService {

    @Resource
    private IServerAreaMapper serverAreaMapper;

    @Override
    public List<ServerArea> getAllByGameId(Integer gameId) {
        return serverAreaMapper.getAllByGameId(gameId);
    }

    @Override
    public ServerArea getServerAreaByParentIdAndCode(Integer parentId, String code) {
        return serverAreaMapper.getServerAreaByParentIdAndCode(parentId, code);
    }
}
