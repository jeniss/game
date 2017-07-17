package com.game.jms.listener;

import com.game.jms.bo.MailBo;
import com.game.jms.convert.ObjectMessageConvert;
import com.game.util.MailUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * Created by jennifert on 7/17/2017.
 */
public class MailListener implements SessionAwareMessageListener<ObjectMessage> {
    private static final Logger LOGGER = Logger.getLogger(MailListener.class);
    @Autowired
    private ObjectMessageConvert objectMessageConvert;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        MailBo mailBo = (MailBo) objectMessageConvert.fromMessage(objectMessage);
        MailUtil.send(mailBo.getFrom(), mailBo.getReplayTo(), mailBo.getMailTo(), mailBo.getCc(), mailBo.getSubject(), mailBo.getMsgContent(), mailBo.getAttachName(), mailBo
                .getAttachFile(), mailBo.getInvitation());
    }
}