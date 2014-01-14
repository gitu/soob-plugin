package org.jenkinsci.plugins.soob;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.logging.Logger;

public class LightNotifier extends Notifier {
    private final static Logger LOGGER = Logger.getLogger(LightNotifier.class
            .getName());
    private final String soobRingId;

    @DataBoundConstructor
    public LightNotifier(String soobRingId) {
        this.soobRingId = soobRingId;
    }

    public String getSoobRingId() {
        return soobRingId;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        sendEmail();
        return true;
    }

    private void sendEmail() {
        String email = getDescriptor().getSoobBoxEmail();
        String subject = "SET_LED";
        String set_ring_data = "";

        Result worst_result = Result.SUCCESS;
        for (AbstractProject project : Jenkins.getInstance().getAllItems(AbstractProject.class)) {
            LightNotifier notifier = (LightNotifier) project.getPublishersList().get(LightNotifier.class);
            if (notifier != null && notifier.getSoobRingId().matches("[01]?[0-9]")) {
                LOGGER.info("job: " + project.getName() + " ring: " + notifier.getSoobRingId());
                String ringId = notifier.getSoobRingId();
                AbstractBuild lastBuild = project.getLastBuild();
                if (lastBuild != null) {
                    if (lastBuild.getResult() != null) {
                        String color = getDescriptor().getColor(lastBuild.getResult().color);
                        set_ring_data += (set_ring_data.length()>0?",":"") + "\"" + ringId + "\":\"" + color + "\"\n";
                    }
                }

                Run lastCompletedBuild = project.getLastCompletedBuild();
                if (lastCompletedBuild != null) {
                    Result lastCompletedBuildResult = lastCompletedBuild.getResult();
                    if (lastCompletedBuildResult != null && lastCompletedBuildResult.isWorseThan(worst_result)) {
                        worst_result = lastCompletedBuildResult;
                    }
                }
            }
        }
        String text = "[";
        text += "{\"action\":\"set_ring\",\"data\":{\n" + set_ring_data + "}},\n";
        text += "{\"action\":\"set_big\", \"data\":\"" + getDescriptor().getColor(worst_result.color) + "\"}\n";
        text += "]";

        MailHelper.sendEmail(email, subject, text);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        private String soobBoxEmail;
        private String soobRED;
        private String soobBLUE;
        private String soobYELLOW;
        private String soobGREY;


        public DescriptorImpl() {
            super(LightNotifier.class);
            load();
        }

        public String getColor(BallColor ballColor) {
            switch (ballColor) {
                case RED:
                case RED_ANIME:
                    return soobRED;
                case YELLOW:
                case YELLOW_ANIME:
                    return soobYELLOW;
                case BLUE:
                case BLUE_ANIME:
                    return soobBLUE;
                case GREY:
                case GREY_ANIME:
                case DISABLED:
                case DISABLED_ANIME:
                case ABORTED:
                case ABORTED_ANIME:
                case NOTBUILT:
                case NOTBUILT_ANIME:
                    return soobGREY;
                default:
                    return soobGREY;
            }
        }

        @Override
        public String getDisplayName() {
            return "Soob Light Notifier";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            soobBoxEmail = formData.getString("soobBoxEmail");
            soobRED = formData.getString("soobRED");
            soobBLUE = formData.getString("soobBLUE");
            soobYELLOW = formData.getString("soobYELLOW");
            soobGREY = formData.getString("soobGREY");

            save();
            return super.configure(req, formData);
        }

        public String getSoobBoxEmail() {
            return soobBoxEmail;
        }

        public void setSoobBoxEmail(String soobBoxEmail) {
            this.soobBoxEmail = soobBoxEmail;
        }

        public String getSoobRED() {
            return soobRED;
        }

        public void setSoobRED(String soobRED) {
            this.soobRED = soobRED;
        }

        public String getSoobBLUE() {
            return soobBLUE;
        }

        public void setSoobBLUE(String soobBLUE) {
            this.soobBLUE = soobBLUE;
        }

        public String getSoobYELLOW() {
            return soobYELLOW;
        }

        public void setSoobYELLOW(String soobYELLOW) {
            this.soobYELLOW = soobYELLOW;
        }

        public String getSoobGREY() {
            return soobGREY;
        }

        public void setSoobGREY(String soobGREY) {
            this.soobGREY = soobGREY;
        }
    }
}