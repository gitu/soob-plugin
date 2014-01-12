package org.jenkinsci.plugins.soob;

import hudson.tasks.Mailer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailHelper {
    private final static Logger LOGGER = Logger.getLogger(LightNotifier.class
            .getName());
    public static void sendEmail(String to, String subject, String text) {
        Session session = Mailer.descriptor().createSession();
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setSubject(subject);
            msg.addRecipients(Message.RecipientType.TO, to);
            msg.setText(text);
            Transport.send(msg);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "error while sending set_led message with text = " + text, e);
        }
    }
}
