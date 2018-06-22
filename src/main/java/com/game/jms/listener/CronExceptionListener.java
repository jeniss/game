package com.game.jms.listener;

import com.game.thread.SeleniumProcessDataThread;
import com.game.util.SpringContextUtil;
import com.game.util.redis.RedisCache;
import com.game.util.redis.RedisKey;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.log4j.Logger;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.jms.JMSException;
import javax.jms.Session;

/**
 * Created by jeniss on 18/6/13.
 */
public class CronExceptionListener implements SessionAwareMessageListener<ActiveMQTextMessage> {
    private static final Logger LOGGER = Logger.getLogger(CronExceptionListener.class);

    @Override
    public void onMessage(ActiveMQTextMessage activeMQTextMessage, Session session) throws JMSException {
        LOGGER.info("----------------------- start to cron exception process data -----------------------");
        try {
            Thread.sleep(1000 * 60);
            // process the data
            ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) SpringContextUtil.getBean("taskExecutor");

            // delete cache data in redis
            RedisCache redisCache = (RedisCache) SpringContextUtil.getBean("redisCache");
            redisCache.delKey(RedisKey.CRON_EXCEPTION_FLAG);

            SeleniumProcessDataThread thread = new SeleniumProcessDataThread();
            taskExecutor.execute(thread);
        } catch (InterruptedException e) {
            LOGGER.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
    }
}
