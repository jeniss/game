package com.game.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jennifert on 7/17/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/applicationContext.xml", "classpath*:/spring-web.xml"})
public class MailUtilTest {
    @Test
    public void send() throws Exception {
        MailUtil.send("jenisstest@163.com", "jeniss1234@163.com", "test", "testhahaha");
    }

}