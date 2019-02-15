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
    /**
    *
    * This method retrieves the jenkins properties from the system properties.
    *
    * @return
    */
    public void initJenkinsConfig() {
        jenkinsBaseUrl = System.getProperty("JENKINS_BASE_URL");
        jenkinsUsername = System.getProperty("JENKINS_USERNAME");
        jenkinsPassword = System.getProperty("JENKINS_PASSWORD");

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
        remremBaseUrl = System.getProperty("REMREM_BASE_URL");

        assertNotNull(remremBaseUrl);
    }

    /**
     *
     * This method retrieves the EI front end properties from the system properties.
     *
     * @return
     */
    public void initEIFrontend() {
        eiFrontendBaseUrl = System.getProperty("EI_FRONTEND_BASE_URL");

        assertNotNull(eiFrontendBaseUrl);
    }
}
