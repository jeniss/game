package com.game;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jennifert on 7/19/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/applicationContext.xml", "classpath*:/spring-web.xml"})
public abstract class BaseTest {

}
