package org.jenkinsci.plugins.soob;

import hudson.model.JobProperty;
import hudson.model.Jobs;
import hudson.model.ViewJob;
import hudson.model.Job;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.tasks.Mailer;
import hudson.views.ViewJobFilter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import jenkins.model.Jenkins;

public class MailBuilder {
    private final static Logger LOGGER = Logger.getLogger(MailBuilder.class
            .getName());

    public void sendMail() {

    }
}
