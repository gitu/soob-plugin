package org.jenkinsci.plugins.soob;

import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.*;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;


public class LightNotifier extends Notifier {
    private final static Logger LOGGER = Logger.getLogger(PluginImpl.class.getName());
	


    private final String ringID;


    @DataBoundConstructor
    public LightNotifier(String ringID) {
        this.ringID = ringID;
    }

    public String getRingtId() {
        return this.ringID;
    }
    
//    @Override
//    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
//        final DescriptorImpl descriptor = this.getDescriptor();
//    	return super.prebuild(build, listener);
//    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        BallColor ballcolor = build.getResult().color;

        Session session = Mailer.descriptor().createSession();
        MimeMessage msg = new MimeMessage(session);
        try {
	        msg.setSubject("SET_LED");
	        msg.addRecipients(RecipientType.TO, getDescriptor().getBoxEmail());
        
			msg.setText("[{'action':'set_ring','data':{"+
			    "'"+getRingtId()+"':'"+ballcolor.getHtmlBaseColor()+"'}}]" );

            Transport.send(msg);
		} catch (MessagingException e) {
			LOGGER.log(Level.SEVERE, "error while sending set_led message", e);
		}
        return true;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

}
