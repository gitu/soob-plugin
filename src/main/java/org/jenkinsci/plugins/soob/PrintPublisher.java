package org.jenkinsci.plugins.soob;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class PrintPublisher extends Notifier {

    @DataBoundConstructor
    public PrintPublisher() {
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
                           BuildListener listener) throws InterruptedException, IOException {

        if (build.getResult().isWorseThan(Result.SUCCESS)) {
            PrintBuildAction action = new PrintBuildAction(build);
            build.addAction(action);
        }

        return true;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }


    @Extension
    @SuppressWarnings("UnusedDeclaration")
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String soobPrintScript;

        public DescriptorImpl() {
            super(PrintPublisher.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Allow broken build Printing";
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
            soobPrintScript = formData.getString("soobPrintScript");

            save();
            return super.configure(req, formData);
        }

        public String getSoobPrintScript() {
            return soobPrintScript;
        }

        public void setSoobPrintScript(String soobPrintScript) {
            this.soobPrintScript = soobPrintScript;
        }
    }


}
