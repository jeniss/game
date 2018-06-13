package com.game.jms.listener;

import com.game.thread.SeleniumProcessDataThread;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * Created by jeniss on 18/6/13.
 */
public class CronExceptionListener implements SessionAwareMessageListener<ObjectMessage> {
    private static final Logger LOGGER = Logger.getLogger(CronExceptionListener.class);
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        try {
            Thread.sleep(1000 * 60 * 30);
            // process the data
            SeleniumProcessDataThread thread = new SeleniumProcessDataThread();
            taskExecutor.execute(thread);
        } catch (InterruptedException e) {
            LOGGER.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
    }
}