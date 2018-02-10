package com.game.jms.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jennifert on 7/17/2017.
 */
public class MailBo implements Serializable {
    private String from;// split by “;”
    private String replayTo;// split by “;”
    private String mailTo;// split by “;”
    private String cc;// split by “;”
    private String subject;
    private String msgContent;
    private List<String> attachments;
    private String invitation;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReplayTo() {
        return replayTo;
    }

    public void setReplayTo(String replayTo) {
        this.replayTo = replayTo;
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }
}
