package com.game.service.impl;

import com.game.model.ServerArea;
import com.game.service.IServerAreaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by jennifert on 5/18/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ServerAreaServiceImplTest {
    @Autowired
    private IServerAreaService IServerAreaService;

    @Test
    public void getAllTest() {
        List<ServerArea> serverAreaList = IServerAreaService.getAll();
        for (ServerArea serverArea : serverAreaList) {
            System.out.println(serverArea.toString());
            if (!CollectionUtils.isEmpty(serverArea.getChildServerAreas())) {
                System.out.println("-----------child " + serverArea.getChildServerAreas().size() + "-------------");
                for (ServerArea child : serverArea.getChildServerAreas()) {
                    System.out.println(child.toString());
                }
            }
        }
    }
}