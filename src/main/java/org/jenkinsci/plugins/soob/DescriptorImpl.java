package org.jenkinsci.plugins.soob;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor.FormException;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.io.IOException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    public static final String FORM_KEY_RING_ID = "ringId";
    public static final String FORM_KEY_BOX_EMAIL = "boxEmail";
    
    private String ringId;
    private String boxEmail;

    public DescriptorImpl() {
        this.load();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Soob Light";
    }

    public FormValidation doCheckBoxEmail(@QueryParameter String value)
            throws IOException, ServletException {
        if (value.length() == 0)
            return FormValidation.error("Please set the Email for the box.");

        return FormValidation.ok();
    }

    public FormValidation doCheckLRingId(@QueryParameter String value)
            throws IOException, ServletException {
        if (value.length() == 0)
            return FormValidation.error("Please set the ID of the ring");
        if (!this.isInteger(value))
            return FormValidation.error("Please enter a number");

        return FormValidation.ok();
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        if (!formData.containsKey(FORM_KEY_RING_ID) || !formData.containsKey(FORM_KEY_BOX_EMAIL))
            return false; // keep client on config page

        this.ringId = formData.getString(FORM_KEY_RING_ID);
        this.boxEmail = formData.getString(FORM_KEY_BOX_EMAIL);
        this.save();
        return super.configure(req, formData);
    }

    public String getRingId() {
        return this.ringId;
    }

    public String getBoxEmail() {
        return this.boxEmail;
    }
}