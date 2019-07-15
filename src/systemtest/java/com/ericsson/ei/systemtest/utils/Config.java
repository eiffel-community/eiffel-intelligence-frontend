package com.ericsson.ei.systemtest.utils;

import static org.junit.Assert.assertNotNull;

import lombok.Getter;

@Getter
public class Config {

    private String jenkinsExternalBaseUrl;
    private String jenkinsInternalBaseUrl;
    private String jenkinsUsername;
    private String jenkinsPassword;

    private String remremBaseUrl;

    private String eiFrontendBaseUrl;
    private String eiBackendBaseUrl;

    private int jobTimeoutMilliseconds = 60000;

    /**
     * Initializes the configuration. Some configurations are set to default and some are mandatory.
     * These are the current default configuration values:
     * int jobTimeoutMilliseconds = 60000
     */
    public Config() {
         String jobTimeoutMilliseconds = System.getProperty("job.timeout.milliseconds");
         if(jobTimeoutMilliseconds != null) {
             this.jobTimeoutMilliseconds = Integer.parseInt(jobTimeoutMilliseconds);
         }
    }

    /**
    *
    * This method retrieves the jenkins properties from the system properties.
    *
    * @return
    */
    public void initJenkinsConfig() {
        jenkinsExternalBaseUrl = System.getProperty("jenkins.external.url");
        jenkinsInternalBaseUrl = System.getProperty("jenkins.internal.url");
        jenkinsUsername = System.getProperty("jenkins.username");
        jenkinsPassword = System.getProperty("jenkins.password");

        assertNotNull(jenkinsExternalBaseUrl);
        assertNotNull(jenkinsInternalBaseUrl);
        assertNotNull(jenkinsUsername);
        assertNotNull(jenkinsPassword);
    }

    /**
    *
    * This method retrieves the remrem properties from the system properties.
    *
    * @return
    */
    public void initRemRemConfig() {
        remremBaseUrl = System.getProperty("remrem.publish.url");

        assertNotNull(remremBaseUrl);
    }

    /**
     *
     * This method retrieves the EI front end properties from the system properties.
     *
     * @return
     */
    public void initEIFrontend() {
        eiFrontendBaseUrl = System.getProperty("ei.frontend.url");

        assertNotNull(eiFrontendBaseUrl);
    }

    public void initEIBackend(String selectedBackend) {
        eiBackendBaseUrl = System.getProperty("ei.backend.url." + selectedBackend);

        assertNotNull(eiBackendBaseUrl);
    }
}
