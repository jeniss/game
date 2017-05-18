package com.game.service.impl;

import com.game.model.ServerArea;
import com.game.service.ServerAreaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by jennifert on 5/18/2017.
 */
@Component
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class ServerAreaServiceImplTest {
    @Resource
    private ServerAreaService serverAreaService;

    @Test
    public void getAllTest() {
        List<ServerArea> serverAreaList = serverAreaService.getAll();
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