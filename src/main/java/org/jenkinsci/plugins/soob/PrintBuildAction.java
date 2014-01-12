package org.jenkinsci.plugins.soob;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.model.BuildBadgeAction;
import hudson.model.ProminentProjectAction;
import hudson.model.Run;
import hudson.model.User;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ExportedBean
@SuppressWarnings("UnusedDeclaration")
public class PrintBuildAction implements BuildBadgeAction, ProminentProjectAction {

    private boolean printed;
    private String printedBy;

    protected Run run;

    public PrintBuildAction(Run run) {
        this.run = run;
    }

    private String reason;

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Build Printer";
    }

    public String getUrlName() {
        return "print";
    }

    public void doPrint(StaplerRequest req, StaplerResponse resp)
            throws ServletException, IOException {
        print();
        Authentication authentication = Jenkins.getAuthentication();
        this.printedBy = authentication.getName();
        this.printed = true;
        resp.forwardToPreviousPage(req);
    }

    @Exported
    public boolean isPrinted() {
        return printed;
    }

    private void print() {
        LightNotifier.DescriptorImpl lightDescriptor = (LightNotifier.DescriptorImpl) Jenkins.getInstance().getDescriptor(LightNotifier.class);
        PrintPublisher.DescriptorImpl printDescriptor = (PrintPublisher.DescriptorImpl) Jenkins.getInstance().getDescriptor(PrintPublisher.class);

        String printText = executeScript(printDescriptor.getSoobPrintScript());

        MailHelper.sendEmail(lightDescriptor.getSoobBoxEmail(), "PRINT", printText);
    }

    private String executeScript(String script) {
        String result = "";
        Map<String, Object> binding = new HashMap<String, Object>();

        binding.put("run", this.run);
        binding.put("project", this.run.getParent());
        binding.put("rooturl", Jenkins.getInstance().getRootUrl());

        GroovyShell shell = createEngine(binding);
        Object res = shell.evaluate(script);
        if (res != null) {
            result = res.toString();
        }
        return result;
    }

    private GroovyShell createEngine(Map<String, Object> variables) {

        ClassLoader cl = Jenkins.getInstance().getPluginManager().uberClassLoader;
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.addCompilationCustomizers(new ImportCustomizer().addStarImports(
                "jenkins",
                "jenkins.model",
                "hudson",
                "hudson.model"));

        Binding binding = new Binding();
        for (Map.Entry<String, Object> e : variables.entrySet()) {
            binding.setVariable(e.getKey(), e.getValue());
        }

        return new GroovyShell(cl, binding, cc);
    }


    public boolean canPrint() {
        return !isPrinted();
    }

    @Exported
    public String getPrintedBy() {
        return printedBy;
    }

    public String getClaimedByName() {
        User user = User.get(printedBy,false,null);
        if (user != null) {
            return user.getDisplayName();
        } else {
            return printedBy;
        }
    }

    public void setPrintedBy(String printedBy) {
        this.printedBy = printedBy;
    }


}
