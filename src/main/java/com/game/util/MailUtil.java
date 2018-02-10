package com.game.util;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by jennifert on 7/17/2017.
 * javaMail
 */
public class MailUtil {
    private static final Logger logger = Logger.getLogger(MailUtil.class);

    public static void send(String from, String mailTo, String subject, String msgContent) {
        send(from, null, mailTo, null, subject, msgContent, null, null);
    }

    public static void send(
            String from, String replayTo, String mailTo, String cc, String subject, String msgContent, List<String> attachments, String invitation) {
        int port = 465;
        Properties props = System.getProperties();
        String host = "smtp.163.com";
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        try {
            // create the connection between application and mail server
            Session msession = Session.getDefaultInstance(props, null);
            msession.setDebug(true);
            Message msg = new MimeMessage(msession);

            // set sender
            InternetAddress addressFrom = new InternetAddress(from);
            msg.setFrom(addressFrom);

            // set receiver
            InternetAddress[] addressTo = parseInternetAddresses(mailTo, ";");
            msg.addRecipients(Message.RecipientType.TO, addressTo);

            InternetAddress[] ccTo = parseInternetAddresses(cc, ";");
            msg.setRecipients(Message.RecipientType.CC, ccTo);

            // set subject
            msg.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));

            if (!StringUtil.isEmpty(replayTo)) {
                InternetAddress[] replyTo = parseInternetAddresses(replayTo, ";");
                msg.setReplyTo(replyTo);
            }

            Multipart mp = new MimeMultipart();
            if (msgContent != null) {
                MimeBodyPart mbp1 = new MimeBodyPart();
                mbp1.setContent(msgContent, "text/html;charset=UTF-8");
                mp.addBodyPart(mbp1);
            }

            if (!CollectionUtils.isEmpty(attachments)) {
                for (String attachFilePath : attachments) {
                    MimeBodyPart mbp2 = new MimeBodyPart();
                    File file = null;
                    file = new File(attachFilePath);
                    if (!file.exists()) {
                        logger.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ":" + String.format("The file(%s) doesn't exist.", attachFilePath));
                        continue;
                    }
                    FileDataSource fds = new FileDataSource(file);
                    mbp2.setDataHandler(new DataHandler(fds));
                    String[] attachFilePathArray = attachFilePath.split(File.separator);
                    String fileName = attachFilePathArray[attachFilePathArray.length - 1];
                    mbp2.setFileName(MimeUtility.encodeWord(fileName));
                    mp.addBodyPart(mbp2);
                }
            }

            if (invitation != null) {
                MimeBodyPart icaPart = new MimeBodyPart();
                icaPart.setDataHandler(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(invitation.getBytes()), "text/calendar;method=REQUEST;charset=\"UTF-8\"")));
                mp.addBodyPart(icaPart);
            }
            msg.setSentDate(new Date());
            msg.setContent(mp);

            Transport transport = msession.getTransport("smtp");
            String username = ConfigHelper.getInstance().getMailUsername();
            String password = ConfigHelper.getInstance().getMailPassword();
            transport.connect(host, username, password);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static InternetAddress[] parseInternetAddresses(String addr, String delimiters) throws MessagingException {
        if (addr == null) {
            return null;
        }
        String[] addrs = addr.split("\\s*" + delimiters.trim() + "\\s*");

        List<InternetAddress> addresses = new ArrayList<InternetAddress>();
        for (String add : addrs) {
            if (add.length() > 0) {
                addresses.add(new InternetAddress(add));
            }
        }
        InternetAddress[] results = new InternetAddress[addresses.size()];
        return addresses.toArray(results);
    }
}
