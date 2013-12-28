package org.jenkinsci.plugins.soob;

import hudson.tasks.Mailer;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

public class MailBuilder {
    private final static Logger LOGGER = Logger.getLogger(PluginImpl.class.getName());

	public void sendMail(DescriptorImpl descriptor) {
		 Session session = Mailer.descriptor().createSession();
	        MimeMessage msg = new MimeMessage(session);
	        try {
		        msg.setSubject("SET_LED");
		        msg.addRecipients(RecipientType.TO, descriptor.getBoxEmail());
	        
				msg.setText("[{'action':'set_ring','data':{"
				   // +"'"+getRingtId()+"':'"+ballcolor.getHtmlBaseColor()+"'}}]" 
						);

	            Transport.send(msg);
			} catch (MessagingException e) {
				LOGGER.log(Level.SEVERE, "error while sending set_led message", e);
			}
	}
}
