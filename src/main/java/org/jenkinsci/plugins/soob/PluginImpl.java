package org.jenkinsci.plugins.soob;

import hudson.Plugin;

import java.util.logging.Logger;


public class PluginImpl extends Plugin {
    private final static Logger LOGGER = Logger.getLogger(PluginImpl.class.getName());

    public void start() throws Exception {
        super.start();
        LOGGER.info("starting soob plugin");
    }
}
