package com.game.util;

import com.game.jms.bo.MailBo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.Destination;

/**
 * Created by jennifert on 7/17/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/applicationContext.xml", "classpath*:/spring-web.xml"})
public class MailUtilTest {
    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    public void send() throws Exception {
        MailUtil.send("jenisstest@163.com", "jeniss1234@163.com", "test", "testhahaha");
    }

    @Test
    public void jmsSend() throws Exception {
        for (int i = 0; i < 3; i++) {
            Destination destination = (Destination) SpringContextUtil.getBean("mailDestination");
            MailBo mailBo = new MailBo();
            mailBo.setFrom("jenisstest@163.com");
            mailBo.setMailTo("jeniss1234@163.com");
            mailBo.setSubject("activemq test -- " + i);
            mailBo.setMsgContent("activemq testhahaha");

            jmsTemplate.convertAndSend(destination, mailBo);
        }

        Thread.sleep(40000);
    }
}