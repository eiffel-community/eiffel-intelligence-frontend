package com.ericsson.ei.systemtest.utils;

import static org.junit.Assert.assertNotNull;

import lombok.Getter;

@Getter
public class Config {

    private String jenkinsBaseUrl;
    private String jenkinsUsername;
    private String jenkinsPassword;

    private String remremBaseUrl;

    private String eiFrontendBaseUrl;
    private String eiBackendBaseUrl;
    /**
    *
    * This method retrieves the jenkins properties from the system properties.
    *
    * @return
    */
    public void initJenkinsConfig() {
        jenkinsBaseUrl = System.getProperty("jenkins.base.url");
        jenkinsUsername = System.getProperty("jenkins.username");
        jenkinsPassword = System.getProperty("jenkins.password");

        assertNotNull(jenkinsBaseUrl);
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
        remremBaseUrl = System.getProperty("remrem.base.url");

        assertNotNull(remremBaseUrl);
    }

    /**
     *
     * This method retrieves the EI front end properties from the system properties.
     *
     * @return
     */
    public void initEIFrontend() {
        eiFrontendBaseUrl = System.getProperty("ei.frontend.base.url");

        assertNotNull(eiFrontendBaseUrl);
    }

    public void initEIBackend() {
        eiBackendBaseUrl = System.getProperty("ei.backend.base.url");

        assertNotNull(eiBackendBaseUrl);
    }
}
